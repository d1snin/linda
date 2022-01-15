package uno.d1s.linda.domain

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "short_link")
class ShortLink(
    @Column(nullable = false, unique = true)
    val url: String,

    @Column(nullable = false, unique = true)
    val alias: String
) {

    @Id
    @Column
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    var id: String? = null

    @Column
    @CreationTimestamp
    var creationTime: Instant? = null

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "shortLink")
    lateinit var redirects: List<Redirect>
}