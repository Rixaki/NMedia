package ru.netology.nmedia.entity

import androidx.room.TypeConverter
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType


class AttachmentConverter {
    @TypeConverter
    fun fromEntAttachment(attEnt: AttachmentEmbeddable): Attachment {
        return Attachment(
            url = attEnt.url,
            description = attEnt.description,
            type = fromEntAttachmentType(attEnt.type)
        )
    }

    @TypeConverter
    fun toEntAttachment(att: Attachment): AttachmentEmbeddable {
        return AttachmentEmbeddable(
            url = att.url,
            description = att.description,
            type = toEntAttachmentType(att.type)
        )
    }

    @TypeConverter
    fun fromEntAttachmentType(attEntType: AttachmentTypeEntity): AttachmentType {
        return AttachmentType.valueOf(attEntType.name)
    }

    @TypeConverter
    fun toEntAttachmentType(attType: AttachmentType): AttachmentTypeEntity {
        return AttachmentTypeEntity.valueOf(attType.name)
    }
}