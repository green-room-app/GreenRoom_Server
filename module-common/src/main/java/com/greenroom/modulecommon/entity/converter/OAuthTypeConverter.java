package com.greenroom.modulecommon.entity.converter;

import com.greenroom.modulecommon.entity.user.OAuthType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class OAuthTypeConverter implements AttributeConverter<OAuthType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(OAuthType oAuthType) {
        return oAuthType.getCode();
    }

    @Override
    public OAuthType convertToEntityAttribute(Integer oAuthTypeCode) {
        return OAuthType.from(oAuthTypeCode);
    }
}
