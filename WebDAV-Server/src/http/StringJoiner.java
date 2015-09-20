package http;

import java.util.function.BinaryOperator;

public class StringJoiner
{
    private StringJoiner()
    { }
    
    public static BinaryOperator<String> join(String separator)
    {
        return (s1, s2) -> s1.isEmpty() ?
                s2 : s1 + separator + s2;
    }
    public static BinaryOperator<String> join()
    {
        return (s1, s2) -> s1.isEmpty() ?
                s2 : s1 + s2;
    }
}
