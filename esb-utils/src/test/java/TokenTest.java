import com.winning.esb.utils.DateUtils;
import com.winning.esb.utils.TokenUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuehao on 2017/9/11.
 */
public class TokenTest {

    public static void main(String[] args) {
        timestampToDatetime();
    }

    private static void timestampToDatetime() {
        long time = 1522147617975L;
        System.out.println(DateUtils.toDateStringSss(time));
    }

    public static void tokenTest() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("test1", "测试1");
        claims.put("test2", "测试2");
        String token = TokenUtils.generateToken(claims);
        System.out.println(token);
    }

}