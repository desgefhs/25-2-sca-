package org.newdawn.spaceinvaders.core;

/**
 * 고해상도 타이머와 정확한 대기(sleep) 기능을 제공하는 유틸리티 클래스.
 * {@link System#nanoTime()}을 사용하여 시간 변화에 영향을 받지 않는 단조 증가 시간을 측정합니다.
 */
public final class SystemTimer {
	/** 고해상도 타이머의 기준 시점 (클래스 로딩 순간). */
	private static final long START_NANOS = System.nanoTime();

	/**
	 * 이 클래스는 인스턴스화될 수 없습니다.
	 */
	private SystemTimer() { /* 유틸 클래스 */ }

	/**
	 * 클래스 초기화 시점부터 경과된 시간을 고해상도 밀리초 단위로 반환합니다.
	 * @return 밀리초 단위의 경과 시간
	 */
	public static long getTime() {
		// nanoTime은 단조 증가(monotonic)하므로 경과 시간 측정에 적합합니다.
		return (System.nanoTime() - START_NANOS) / 1_000_000L;
	}

	/**
	 * 지정된 시간(ms) 동안 현재 스레드를 대기시킵니다.
	 * {@link Thread#sleep(long, int)}을 사용하여 일반적인 sleep보다 더 정확한 대기를 시도합니다.
	 * @param duration 대기할 시간 (밀리초)
	 */
	public static void sleep(long duration) {
		if (duration <= 0) return;

		final long deadline = System.nanoTime() + duration * 1_000_000L;
		while (true) {
			long remainingNanos = deadline - System.nanoTime();
			if (remainingNanos <= 0) break;

			long millis = remainingNanos / 1_000_000L;
			int nanos = (int) (remainingNanos % 1_000_000L);

			try {
				Thread.sleep(millis, nanos);
			} catch (InterruptedException ie) {
				// 인터럽트 발생 시, 현재 스레드의 인터럽트 상태를 복원하고 루프를 중단합니다.
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
}