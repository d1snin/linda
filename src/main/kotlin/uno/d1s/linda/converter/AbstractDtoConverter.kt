package uno.d1s.linda.converter

abstract class AbstractDtoConverter<E, D> : DtoConverter<E, D> {

    override fun convertToDtoList(entities: List<E>): List<D> =
        entities.map {
            this.convertToDto(it)
        }

    override fun convertToEntityList(dtoList: List<D>): List<E> =
        dtoList.map {
            this.convertToEntity(it)
        }
}