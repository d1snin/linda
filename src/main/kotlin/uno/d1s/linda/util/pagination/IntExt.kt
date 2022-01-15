package uno.d1s.linda.util.pagination

val Int?.thisOrDefaultPageSize get() = this ?: 20
val Int?.thisOrDefaultPage get() = this ?: 0