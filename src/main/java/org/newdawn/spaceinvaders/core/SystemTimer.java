package org.newdawn.spaceinvaders.core;

/*
 * 게임 루프의 시간 측정 기능에 사용
 */
public final class SystemTimer {

	private static final long START_NANOS = System.nanoTime();
	/** "타이머 틱/초" 개념 (1초 = 1,000,000,000 틱) */
	@SuppressWarnings("unused")
	private static final long TIMER_TICKS_PER_SECOND = 1_000_000_000L;

	private SystemTimer() {}
	/// 클래스 초기화 이후 경과된 시간을 고해상도 밀리초 단위로 반환
	/// System.nanoTime()을 사용하여 증가하는 시간을 보장
	///
	/// @return 밀리초 단위의 경과 시간
	public static long getTime() {
		return (System.nanoTime() - START_NANOS) / 1_000_000L;
	}

	/**
	 * 지정된 시간(밀리초) 동안 현재 스레드를 정밀하게 대기
	 * Thread.sleep()을 사용하지만, System.nanoTime()을 이용해 더 정확한 대기 시간을 보장하려 시도
	 *
	 * @param duration 대기할 시간 (밀리초)
	 */
	public static void sleep(long duration) {
		if (duration <= 0) return;

		final long deadline = System.nanoTime() + duration * 1_000_000L;
		while (true) {
			long remaining = deadline - System.nanoTime();
			if (remaining <= 0) break;

			long millis = remaining / 1_000_000L;
			int nanos = (int) (remaining - millis * 1_000_000L);
			try {
				// 남은 시간이 1ms 이상이면 ms+nanos로, 아주 짧으면 0ms + nanos로 잠깐 쉼
				Thread.sleep(millis, nanos);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
}