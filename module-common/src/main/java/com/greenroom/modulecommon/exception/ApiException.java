package com.greenroom.modulecommon.exception;

import com.greenroom.modulecommon.util.MessageUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Getter
public class ApiException extends ServiceRuntimeException {

    private final EnumApiException type;

    /**
     * ApiException TYPE1
     *
     * Usage: 단순히 어떤 타입의 예외이고, 추가적으로 message만 기입하고 싶을 때
     */
    public ApiException(EnumApiException type, String message) {
        super(new String[]{message});
        this.type = type;
    }

    /**
     * ApiException TYPE2
     *
     * Usage: Entity 데이터 조회 전용. 어떤 Entity Class를 못 찾았고, 그 때의 해당 parameter return
     */
    public ApiException(EnumApiException type, Class<?> cls, Object... values) {
        this(type, cls.getSimpleName(), values);
    }

    private ApiException(EnumApiException type, String targetName, Object... values) {
        super(new String[]{targetName,
                (isNotEmpty(values)) ? StringUtils.join(values, ",") : ""});

        this.type = type;
    }

    /**
     * 해당 Exception 출력 시 message.properties의 detailKey를 읽고, 거기에 해당 parameter를 넣어줌
     */
    @Override
    public String getMessage() {
        return MessageUtils.getMessage(type.getMessageDetailKey(), getParams());
    }

    @Override
    public String toString() {
        return MessageUtils.getMessage(type.getMessageKey());
    }
}
