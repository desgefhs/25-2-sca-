
package org.newdawn.spaceinvaders.auth;

/**
 * 인증된 사용자 정보를 저장하는 클래스입니다.
 */
public class AuthenticatedUser {
    /** 사용자의 고유 ID */
    private final String localId;
    /** 사용자의 이름 */
    private final String username;

    /**
     * AuthenticatedUser 객체를 생성합니다.
     *
     * @param localId 사용자의 고유 ID
     * @param username 사용자의 이름
     */
    public AuthenticatedUser(String localId, String username) {
        this.localId = localId;
        this.username = username;
    }

    /**
     * 사용자의 고유 ID를 반환합니다.
     *
     * @return 사용자 고유 ID
     */
    public String getLocalId() {
        return localId;
    }

    /**
     * 사용자의 이름을 반환합니다.
     *
     * @return 사용자 이름
     */
    public String getUsername() {
        return username;
    }
}
