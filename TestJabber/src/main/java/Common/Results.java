package Common;

/**
 * Результат одного сообщения
 * Отправляется от серверов к локальной машине
 * По результатам этого строятся графики
 * С SUCCESS отправляется время задержки
 * С FAIL не отправляется ничего
 */
public class  Results {
    public static final int SUCCESS = 1;
    public static final int FAIL = 2;
}
