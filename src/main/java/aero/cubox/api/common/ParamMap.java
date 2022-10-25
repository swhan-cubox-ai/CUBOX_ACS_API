package aero.cubox.api.common;

import com.amazonaws.util.StringUtils;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.springframework.jdbc.support.JdbcUtils;

public class ParamMap extends ListOrderedMap {

    public Object put(Object key, Object value)
    {
        return super.put(JdbcUtils.convertUnderscoreNameToPropertyName(StringUtils.lowerCase((String) key)), value);
    }
}
