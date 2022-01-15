package uno.d1s.linda.util.pagination

import org.springframework.beans.support.PagedListHolder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

fun <T> List<T>.toPage(page: Int?, size: Int?): Page<T> {
    val notNullPage = page.thisOrDefaultPage
    val notNullPageSize = size.thisOrDefaultPageSize

    return PageImpl(PagedListHolder(this).apply {
        this.page = notNullPage
        this.pageSize = notNullPageSize
    }.pageList, PageRequest.of(notNullPage, notNullPageSize), this.size.toLong())
}