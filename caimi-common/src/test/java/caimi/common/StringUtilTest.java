package caimi.common;

import java.util.ArrayList;
import java.util.List;

import com.caimi.util.StringUtil;

public class StringUtilTest {

    public static void main(String[] args) {
        List<String[]> result = new ArrayList<>();
        result = StringUtil.splitKVs("name=wl,sex=man");
        System.out.println(result.toString());
    }

}
