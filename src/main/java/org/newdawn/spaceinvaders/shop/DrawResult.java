package org.newdawn.spaceinvaders.shop;

/**
 * 아이템 뽑기 시도의 결과를 담는 데이터 클래스.
 * 뽑기 성공 여부와 사용자에게 표시될 메시지를 포함합니다.
 */
public class DrawResult {
    /** 결과 메시지 (예: "크레딧 부족!", "'공격형 펫' 획득!"). */
    private final String message;
    /** 뽑기 성공 여부. */
    private final boolean success;

    /**
     * DrawResult 생성자.
     *
     * @param message 사용자에게 표시할 결과 메시지
     * @param success 뽑기 성공 여부
     */
    public DrawResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    /**
     * 결과 메시지를 반환합니다.
     * @return 결과 메시지
     */
    public String getMessage() {
        return message;
    }

    /**
     * 뽑기가 성공했는지 여부를 반환합니다.
     * @return 성공 시 true, 실패 시 false
     */
    public boolean isSuccess() {
        return success;
    }
}
