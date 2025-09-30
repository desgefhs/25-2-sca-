package org.newdawn.spaceinvaders.core;

import com.google.cloud.firestore.Firestore;
import org.newdawn.spaceinvaders.auth.AuthenticatedUser;
import org.newdawn.spaceinvaders.data.DatabaseManager;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.player.PlayerStats;
import org.newdawn.spaceinvaders.shop.ShopManager;
import org.newdawn.spaceinvaders.shop.Upgrade;
import org.newdawn.spaceinvaders.view.Background;
import org.newdawn.spaceinvaders.view.ConfirmDialog;
import org.newdawn.spaceinvaders.view.GameWindow;
import org.newdawn.spaceinvaders.view.MainMenu;
import org.newdawn.spaceinvaders.view.PauseMenu;
import org.newdawn.spaceinvaders.view.GameOverMenu;
import org.newdawn.spaceinvaders.view.ShopMenu;

/**
 * 게임의 핵심 로직과 상태를 관리하며, 다른 전문 관리 클래스들을 총괄하는 메인 컨트롤러.
 */
public class GameManager implements GameContext {

    private final InputHandler inputHandler;
    private final EntityManager entityManager;
    private final CollisionDetector collisionDetector;
    private final GameWindow gameWindow;
    private final MainMenu mainMenu;
    private final PauseMenu pauseMenu;
    private final GameOverMenu gameOverMenu;
    private final ConfirmDialog confirmDialog;
    private final DatabaseManager databaseManager;
    private final AuthenticatedUser user;
    private final ShopManager shopManager;
    private final Background background;
    private ShopMenu shopMenu;
    private PlayerStats playerStats;


    private PlayerData currentPlayer;

    private GameState currentState;
    private boolean gameRunning = true;
    private boolean logicRequiredThisLoop = false;
    private boolean showHitboxes = false;
    private String message = "";
    private long lastFire = 0;
    private final double moveSpeed = 300;
    private static final int ALIEN_SCORE = 10;
    private int score = 0;
    private int wave = 1;
    private int lineCount = 0;
    private long lastLineSpawnTime = 0;
    private final long lineSpawnInterval = 3000;
    private final int LINES_PER_WAVE = 10;

    private long lastMeteorSpawnTime = 0;
    private final long meteorSpawnInterval = 3000; // 3 seconds
    private long lastBombSpawnTime = 0;
    private final long bombSpawnInterval = 5000; // 5 seconds

    public GameManager(AuthenticatedUser user, Firestore db) {
        this.user = user;
        this.databaseManager = new DatabaseManager(db);
        this.inputHandler = new InputHandler();
        this.entityManager = new EntityManager(this);
        this.collisionDetector = new CollisionDetector();
        this.gameWindow = new GameWindow(inputHandler);
        this.mainMenu = new MainMenu();
        this.pauseMenu = new PauseMenu();
        this.gameOverMenu = new GameOverMenu();
        this.shopManager = new ShopManager();
        this.background = new Background("sprites/gamebackground.png");
        this.playerStats = new PlayerStats();
        this.confirmDialog = new ConfirmDialog("Are you sure you want to exit?");
        setCurrentState(GameState.MAIN_MENU);
    }

    public void initializePlayer() {
        this.currentPlayer = databaseManager.loadPlayerData(user.getLocalId(), user.getUsername());
        // Initialize ShopMenu with all available upgrades
        this.shopMenu = new ShopMenu(shopManager.getAllUpgrades());
    }

    public void startGame() {
        gameWindow.setVisible(true);
        mainLoop();
    }

    private void setCurrentState(GameState newState) {
        this.currentState = newState;
    }

    private void mainLoop() {
        long lastLoopTime = SystemTimer.getTime();
        while (gameRunning) {
            long delta = SystemTimer.getTime() - lastLoopTime;
            lastLoopTime = SystemTimer.getTime();

            switch (currentState) {
                case MAIN_MENU:
                    handleMenuInput();
                    gameWindow.getGameCanvas().renderMenu(mainMenu);
                    break;
                case RANKING:
                    if (inputHandler.isFirePressedAndConsume()) {
                        setCurrentState(GameState.MAIN_MENU);
                    }
                    // 랭킹 렌더링 시 DB매니저로부터 직접 데이터를 가져와 전달
                    gameWindow.getGameCanvas().renderRanking(databaseManager.getHighScores());
                    break;
                case SHOP:
                    handleShopInput();
                    gameWindow.getGameCanvas().renderShop(shopMenu, currentPlayer, message);
                    break;
                case PLAYING:
                    updateGame(delta);
                    break;
                case PAUSED:
                    handlePauseMenuInput();
                    gameWindow.getGameCanvas().render(background, entityManager.getEntities(), message, score, currentState, wave, pauseMenu, gameOverMenu, showHitboxes);
                    break;
                case GAME_OVER:
                case GAME_WON:
                    if (inputHandler.isLeftPressedAndConsume()) gameOverMenu.moveLeft();
                    if (inputHandler.isRightPressedAndConsume()) gameOverMenu.moveRight();

                    if (inputHandler.isFirePressedAndConsume()) {
                        String selected = gameOverMenu.getSelectedItem();
                        if ("다시하기".equals(selected)) {
                            startGameplay();
                        } else if ("메인 메뉴로".equals(selected)) {
                            setCurrentState(GameState.MAIN_MENU);
                        }
                    }
                    gameWindow.getGameCanvas().render(background, entityManager.getEntities(), message, score, currentState, wave, pauseMenu, gameOverMenu, showHitboxes);
                    break;
                case WAVE_CLEARED:
                    startNextWave();
                    break;
                case EXIT_CONFIRMATION:
                    handleExitConfirmationInput();
                    gameWindow.getGameCanvas().renderConfirmDialog(confirmDialog);
                    break;
            }
            SystemTimer.sleep(lastLoopTime + 10 - SystemTimer.getTime());
        }
    }

    private void updateGame(long delta) {
        if (inputHandler.isHPressedAndConsume()) {
            showHitboxes = !showHitboxes;
        }
        if (inputHandler.isEscPressedAndConsume()) {
            setCurrentState(GameState.PAUSED);
            return;
        }
        background.update(delta);
        handlePlayingInput(delta);
        handleSpawning(delta);
        updateEntities(delta);

        if (logicRequiredThisLoop) {
            entityManager.doLogicAll();
            logicRequiredThisLoop = false;
        }
        gameWindow.getGameCanvas().render(background, entityManager.getEntities(), message, score, currentState, wave, pauseMenu, gameOverMenu, showHitboxes);
    }

    /**
     * 모든 종류의 엔티티(외계인, 운석, 폭탄) 생성을 처리합니다.
     */
    private void handleSpawning(long delta) {
        int effectiveWave = ((wave - 1) % 5) + 1;
        // Spawn aliens
        if (effectiveWave == 5) { // Boss Wave
            if (lineCount == 0) {
                entityManager.spawnNext(wave, lineCount);
                lineCount++; // Increment to prevent re-spawning
            }
        } else { // Normal Wave
            if (lineCount < LINES_PER_WAVE) {
                if (System.currentTimeMillis() - lastLineSpawnTime > lineSpawnInterval) {
                    entityManager.spawnNext(wave, lineCount);
                    lineCount++;
                    lastLineSpawnTime = System.currentTimeMillis();
                }
            }
        }

        long currentTime = System.currentTimeMillis();

        // Spawn meteors
        if (currentTime - lastMeteorSpawnTime > meteorSpawnInterval) {
            lastMeteorSpawnTime = currentTime;
            int quantity = (int) (Math.random() * 2) + 2; // 2 or 3
            for (int i = 0; i < quantity; i++) {
                MeteorEntity meteor = new MeteorEntity(this, "sprites/meteor.gif", 0, -50, 150);
                int x = (int) (Math.random() * (Game.GAME_WIDTH - meteor.getWidth()));
                meteor.setX(x);
                entityManager.addEntity(meteor);
            }
        }

        // Spawn bombs
        if (currentTime - lastBombSpawnTime > bombSpawnInterval) {
            lastBombSpawnTime = currentTime;
            int quantity = (int) (Math.random() * 2) + 2; // 2 or 3
            for (int i = 0; i < quantity; i++) {
                BombEntity bomb = new BombEntity(this, "sprites/bomb.gif", 0, -50, 100, wave);
                int x = (int) (Math.random() * (Game.GAME_WIDTH - bomb.getWidth()));
                bomb.setX(x);
                entityManager.addEntity(bomb);
            }
        }
    }

    /**
     * 엔티티의 이동, 충돌, 제거 등 상태 업데이트를 처리합니다.
     */
    private void updateEntities(long delta) {
        entityManager.moveAll(delta);
        collisionDetector.checkCollisions(entityManager.getEntities());
        entityManager.cleanup();
    }

    private void handleMenuInput() {
        if (inputHandler.isLeftPressedAndConsume()) mainMenu.moveLeft();
        if (inputHandler.isRightPressedAndConsume()) mainMenu.moveRight();
        if (inputHandler.isEscPressedAndConsume()) {
            setCurrentState(GameState.EXIT_CONFIRMATION);
        }

        if (inputHandler.isFirePressedAndConsume()) {
            String selected = mainMenu.getSelectedItem();
            if ("1. 게임시작".equals(selected)) {
                startGameplay();
            } else if ("2. 랭킹".equals(selected)) {
                setCurrentState(GameState.RANKING);
            } else if ("4. 상점".equals(selected)) {
                message = ""; // Clear message before entering shop
                setCurrentState(GameState.SHOP);
            } else if ("5. 설정".equals(selected)){
                System.exit(0);
            }
        }
    }

    private void handleExitConfirmationInput() {
        if (inputHandler.isLeftPressedAndConsume()) confirmDialog.moveLeft();
        if (inputHandler.isRightPressedAndConsume()) confirmDialog.moveRight();

        if (inputHandler.isFirePressedAndConsume()) {
            String selected = confirmDialog.getSelectedItem();
            if ("Confirm".equals(selected)) {
                System.exit(0);
            } else if ("Cancel".equals(selected)) {
                setCurrentState(GameState.MAIN_MENU);
            }
        }
    }

    private void handleShopInput() {
        if (inputHandler.isUpPressedAndConsume()) shopMenu.moveUp();
        if (inputHandler.isDownPressedAndConsume()) shopMenu.moveDown();
        if (inputHandler.isEscPressedAndConsume()) setCurrentState(GameState.MAIN_MENU);


        if (inputHandler.isFirePressedAndConsume()) {
            Upgrade selectedUpgrade = shopMenu.getSelectedItem();
            if (selectedUpgrade == null) return;

            int currentLevel = currentPlayer.getUpgradeLevel(selectedUpgrade.getId());
            if (currentLevel >= selectedUpgrade.getMaxLevel()) {
                message = "이미 최고 레벨입니다.";
                return; // Already at max level
            }

            int cost = selectedUpgrade.getCost(currentLevel + 1);
            if (currentPlayer.getCredit() >= cost) {
                // 1. Deduct credit
                currentPlayer.setCredit(currentPlayer.getCredit() - cost);

                // 2. Increase upgrade level
                currentPlayer.setUpgradeLevel(selectedUpgrade.getId(), currentLevel + 1);

                // 3. Save to database
                databaseManager.updatePlayerData(user.getLocalId(), currentPlayer);

                message = "업그레이드 성공!";
            } else {
                message = "크레딧이 부족합니다!";
            }
        }
    }
    
    private void handlePauseMenuInput() {
        if (inputHandler.isUpPressedAndConsume()) pauseMenu.moveUp();
        if (inputHandler.isDownPressedAndConsume()) pauseMenu.moveDown();
        if (inputHandler.isFirePressedAndConsume()) {
            String selected = pauseMenu.getSelectedItem();
            if ("재개하기".equals(selected)) setCurrentState(GameState.PLAYING);
            else if ("메인메뉴로 나가기".equals(selected)) {
                saveGameResults(); // 일시정지 후 메뉴로 나갈때도 결과 저장
                setCurrentState(GameState.MAIN_MENU);
            }
            else if ("종료하기".equals(selected)) System.exit(0);
        }
    }

    private void calculatePlayerStats() {
        playerStats = new PlayerStats(); // Reset to default stats

        for (Upgrade upgrade : shopManager.getAllUpgrades()) {
            int level = currentPlayer.getUpgradeLevel(upgrade.getId());
            if (level > 0) {
                switch (upgrade.getId()) {
                    case "DAMAGE":
                        playerStats.setBulletDamage((int) upgrade.getEffect(level));
                        break;
                    case "HEALTH":
                        playerStats.setMaxHealth((int) upgrade.getEffect(level));
                        break;
                    case "ATK_SPEED":
                        playerStats.setFiringInterval((long) upgrade.getEffect(level));
                        break;
                    case "PROJECTILE":
                        playerStats.setProjectileCount((int) upgrade.getEffect(level));
                        break;
                }
            }
        }
    }

    private void startGameplay() {
        calculatePlayerStats();
        resetScore();
        wave = 1;
        lineCount = 0;
        lastLineSpawnTime = System.currentTimeMillis();
        entityManager.initShip(playerStats);
        entityManager.getShip().reset(); // Reset ship state for a new game
        setCurrentState(GameState.PLAYING);
        message = "";
    }

    private void handlePlayingInput(long delta) {
        ShipEntity ship = entityManager.getShip();
        if (ship == null) return;
        ship.setHorizontalMovement(0);
        ship.setVerticalMovement(0);

        if (inputHandler.isLeftPressed() && !inputHandler.isRightPressed()) ship.setHorizontalMovement(-moveSpeed);
        else if (inputHandler.isRightPressed() && !inputHandler.isLeftPressed()) ship.setHorizontalMovement(moveSpeed);

        if (inputHandler.isUpPressed() && !inputHandler.isDownPressed()) ship.setVerticalMovement(-moveSpeed);
        if (inputHandler.isDownPressed() && !inputHandler.isUpPressed()) ship.setVerticalMovement(moveSpeed);

        if (inputHandler.isFirePressed()) tryToFire();

        // For debugging: Press 'k' to skip to the next boss wave
        if (inputHandler.isKPressedAndConsume()) {
            int targetWave = ((wave / 5) * 5) + 5;
            setWave(targetWave);
            startNextWave();
        }
    }

    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < playerStats.getFiringInterval()) return;
        lastFire = System.currentTimeMillis();
        ShipEntity ship = entityManager.getShip();

        for (int i=0; i < playerStats.getProjectileCount(); i++) {
            // This is a simple implementation for multi-shot.
            // A better way would be to spread the shots.
            int xOffset = (i - playerStats.getProjectileCount() / 2) * 15;
            ShotEntity shot = new ShotEntity(this, "sprites/shot.gif", ship.getX() + 10 + xOffset, ship.getY() - 30, playerStats.getBulletDamage());
            entityManager.addEntity(shot);
        }
    }

    private void saveGameResults() {
        if (user == null || currentPlayer == null) return;

        // 1. Update the currentPlayer object with the new results
        currentPlayer.setCredit(currentPlayer.getCredit() + score);
        currentPlayer.setHighScore(Math.max(currentPlayer.getHighScore(), score));

        // 2. Save the entire updated PlayerData object to the database
        databaseManager.updatePlayerData(user.getLocalId(), currentPlayer);
    }

    @Override
    public void notifyDeath() {
        int roundScore = score;
        saveGameResults();
        long finalCredit = currentPlayer.getCredit();

        message = String.format("이번 라운드 점수: %d / 최종 크레딧: %d", roundScore, finalCredit);
        setCurrentState(GameState.GAME_OVER);
    }

    @Override
    public void notifyWin() {
        saveGameResults();
        message = "Well done! You Win!";
        setCurrentState(GameState.GAME_WON);
    }
    
    @Override
    public void addEntity(Entity entity) { entityManager.addEntity(entity); }
    @Override
    public void removeEntity(Entity entity) { entityManager.removeEntity(entity); }
    @Override
    public void notifyAlienKilled() { 
        increaseScore(ALIEN_SCORE);
        entityManager.decreaseAlienCount();

        if (entityManager.getAlienCount() == 0) {
            int effectiveWave = ((wave - 1) % 5) + 1;
            boolean isBossWave = (effectiveWave == 5);

            if (isBossWave || lineCount >= LINES_PER_WAVE) {
                setCurrentState(GameState.WAVE_CLEARED);
            }
        }
    }
    @Override
    public void updateLogic() { logicRequiredThisLoop = true; }
    @Override
    public void notifyAlienEscaped(Entity entity) {
        entityManager.removeEntity(entity);
        entityManager.decreaseAlienCount();
        if (entityManager.getAlienCount() == 0 && lineCount >= LINES_PER_WAVE) {
            setCurrentState(GameState.WAVE_CLEARED);
        }
    }

    @Override
    public java.util.List<Entity> getEntities() {
        return entityManager.getEntities();
    }
    public void increaseScore(int amount) { score += amount; }
    public void resetScore() { score = 0; }
    public int getScore() { return score; }


    public void setWave(int newWave) {
        this.wave = newWave - 1; // startNextWave will increment it to the target wave
    }

     public void startNextWave() {
        wave++;
        lineCount = 0;
        lastLineSpawnTime = System.currentTimeMillis();
        message = "Wave " + wave;
        entityManager.initShip(playerStats);
        setCurrentState(GameState.PLAYING);
    }
}