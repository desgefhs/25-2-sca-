package org.newdawn.spaceinvaders;

/**
 * 게임의 현재 상태를 나타내는 열거형(enum).
 * MAIN_MENU: 메인 메뉴 화면
 * PLAYING: 게임 플레이 중
 * GAME_OVER: 플레이어가 사망하여 "Press any key"를 기다리는 상태
 */
public enum GameState {
    MAIN_MENU,
    PLAYING,
    PAUSED,
    WAVE_CLEARED,
    GAME_OVER,
    GAME_WON
}