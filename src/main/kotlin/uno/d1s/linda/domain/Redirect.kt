package uno.d1s.linda.domain

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "redirect")
class Redirect(
    @ManyToOne(cascade = [CascadeType.ALL])
    val shortLink: ShortLink
) {

    @Id
    @Column
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    var id: String? = null

    @Column
    @CreationTimestamp
    var creationTime: Instant? = null
}