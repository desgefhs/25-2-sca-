package org.newdawn.spaceinvaders.userinput.command;

/**
 * 실행될 액션을 나타내는 커맨드 패턴(Command Pattern)의 핵심 인터페이스.
 * 이 인터페이스를 구현하는 클래스는 특정 동작을 캡슐화합니다.
 */
public interface Command {
    /**
     * 이 커맨드와 연관된 액션을 실행합니다.
     */
    void execute();
}
