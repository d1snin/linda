package uno.d1s.linda.converter

interface DtoConverter<E, D> {

    fun convertToDto(entity: E): D

    fun convertToEntity(dto: D): E

    fun convertToDtoList(entities: List<E>): List<D>

    fun convertToEntityList(dtoList: List<D>): List<E>
}