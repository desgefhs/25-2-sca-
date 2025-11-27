
package org.newdawn.spaceinvaders.auth;

/**
 * 성공적으로 인증된(로그인된) 사용자의 정보를 담는 데이터 클래스.
 * 사용자의 고유 ID와 사용자 이름을 저장합니다.
 */
public class AuthenticatedUser {
    /** Firestore에 저장된 사용자의 고유 문서 ID (UID). */
    private final String localId;
    /** 사용자의 이름. */
    private final String username;

    /**
     * AuthenticatedUser 생성자.
     *
     * @param localId 사용자의 고유 ID
     * @param username 사용자의 이름
     */
    public AuthenticatedUser(String localId, String username) {
        this.localId = localId;
        this.username = username;
    }

    /**
     * 사용자의 고유 ID(UID)를 반환합니다.
     * @return 사용자의 고유 ID
     */
    public String getLocalId() {
        return localId;
    }

    /**
     * 사용자의 이름을 반환합니다.
     * @return 사용자 이름
     */
    public String getUsername() {
        return username;
    }
}
