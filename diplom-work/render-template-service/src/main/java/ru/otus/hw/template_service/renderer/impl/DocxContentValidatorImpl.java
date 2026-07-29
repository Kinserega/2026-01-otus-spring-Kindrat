package ru.otus.hw.template_service.renderer.impl;

import org.springframework.stereotype.Component;
import ru.otus.hw.template_service.renderer.DocxContentValidator;

@Component
public class DocxContentValidatorImpl implements DocxContentValidator {

    private static final int ZIP_SIGNATURE_LENGTH = 4;

    private static final int ZIP_SIGNATURE_FIRST_BYTE = 0x50;

    private static final int ZIP_SIGNATURE_SECOND_BYTE = 0x4B;

    private static final int ZIP_SIGNATURE_THIRD_BYTE = 0x03;

    private static final int ZIP_SIGNATURE_FOURTH_BYTE = 0x04;

    @Override
    public boolean isDocx(byte[] documentContent) {
        if (documentContent == null
                || documentContent.length < ZIP_SIGNATURE_LENGTH) {
            return false;
        }
        return Byte.toUnsignedInt(documentContent[0]) == ZIP_SIGNATURE_FIRST_BYTE
                && Byte.toUnsignedInt(documentContent[1]) == ZIP_SIGNATURE_SECOND_BYTE
                && Byte.toUnsignedInt(documentContent[2]) == ZIP_SIGNATURE_THIRD_BYTE
                && Byte.toUnsignedInt(documentContent[3]) == ZIP_SIGNATURE_FOURTH_BYTE;
    }
}