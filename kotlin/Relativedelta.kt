import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.min
import kotlin.math.sign
 
 
internal class Relativedelta(baseDay: LocalDateTime, day: LocalDateTime) {
  private val year: Int? = null
  private val month: Int? = null
  private val day: Int? = null
  private val hour: Int? = null
  private val minute: Int? = null
  private val second: Int? = null
  private var years = 0
  private var months = 0
  private var days = 0
  private var hours = 0
  private var minutes = 0
  private var seconds = 0
  private val leapdays = 0
  private var map: HashMap<String, Int>? = null
 
  init {
    // 日付が基準日より前の場合は例外とする
    require(day.compareTo(baseDay) != -1)
    var months = (day.year - baseDay.year) * 12 + (day.monthValue - baseDay.monthValue)
    setMonths(months)
    val baseSec = baseDay.toEpochSecond(ZoneOffset.systemDefault().rules.getOffset(baseDay))
    val daySec = day.toEpochSecond(ZoneOffset.systemDefault().rules.getOffset(day))
    var dtm = getDTM(baseDay)
    var compare: MyCompare? = null
    var increment = 0
    if (daySec < baseSec) {
      compare = MyCompare(true)
      increment = 1
    } else {
      compare = MyCompare(false)
      increment = -1
    }
    while (compare.compare(day, dtm) == 1) {
      months += increment
      setMonths(months)
      dtm = getDTM(baseDay)
    }
    val duration = Duration.between(dtm, day)
    this.seconds = duration.toSeconds().toInt()
    if (this.seconds > 59) {
      val s = sign(this.seconds.toDouble()).toInt()
      val div = Math.floorDiv(this.seconds * s, 60)
      val mod = (this.seconds * s) % 60
      this.seconds = mod * s
      this.minutes += div * s
    }
    if (this.minutes > 59) {
      val s = sign(this.minutes.toDouble()).toInt()
      val div = Math.floorDiv(this.minutes * s, 60)
      val mod = (this.minutes * s) % 60
      this.minutes = mod * s
      this.hours += div * s
    }
    if (this.hours > 23) {
      val s = sign(this.hours.toDouble()).toInt()
      val div = Math.floorDiv(this.hours * s, 24)
      val mod = (this.hours * s) % 24
      this.hours = mod * s
      this.days += div * s
    }
    if (months > 11) {
      val s = sign(this.months.toDouble()).toInt()
      val div = Math.floorDiv(this.months * s, 12)
      val mod = (this.months * s) % 12
      this.months = mod * s
      this.years += div * s
    }
    val map = HashMap<String, Int>()
    map["y"] = this.years
    map["m"] = this.months
    map["d"] = this.days
    map["hh"] = this.hours
    map["mm"] = this.minutes
    map["ss"] = this.seconds
    this.map = map
  }
 
  private fun setMonths(months: Int) {
    this.months = months
    if (this.months > 11) {
      val s = sign(this.months.toDouble()).toInt()
      val div = Math.floorDiv(this.months * s, 12)
      val mod = (this.months * s) % 12
      this.months = mod * s
      this.years = div * s
    } else {
      this.years = 0
    }
  }
 
  private fun getDTM(other: LocalDateTime): LocalDateTime {
    var y = 0
    if (this.year != null) {
      y = this.year + this.years
    } else if (other.year > 0) {
      y = other.year + this.years
    }
    var m = this.month ?: other.monthValue
    if (this.months > 0) {
      m += this.months
      if (m > 12) {
        y += 1
        m -= 12
      } else if (m < 1) {
        y -= 1
        m += 12
      }
    }
    val _d = this.day ?: other.dayOfMonth
    val d =
      min(LocalDateTime.of(y, m, 1, 0, 0, 0).toLocalDate().lengthOfMonth().toDouble(), _d.toDouble())
        .toInt()
    val repl = HashMap<String, Int?>()
    repl["year"] = y
    repl["month"] = m
    repl["day"] = d
    repl["hour"] = this.hour ?: other.hour
    repl["minute"] = this.minute ?: other.minute
    repl["second"] = this.second ?: other.second
    var days = this.days
    val ldt = LocalDateTime.of(y, 1, 1, 0, 0, 0)
    if (this.leapdays > 0 && m > 2 && ldt.toLocalDate().isLeapYear) {
      days += this.leapdays
    }
    val ret = LocalDateTime.of(
      repl["year"]!!,
      repl["month"]!!,
      repl["day"]!!,
      repl["hour"]!!,
      repl["minute"]!!,
      repl["second"]!!
    )
    return ret.plusDays(days.toLong()).plusHours(this.hours.toLong()).plusMinutes(this.minutes.toLong())
      .plusSeconds(this.seconds.toLong())
  }
 
  fun getMap(): HashMap<String, Int>? {
    return this.map
  }
 
  private inner class MyCompare(private val isGT: Boolean) : Comparator<LocalDateTime> {
    override fun compare(o1: LocalDateTime, o2: LocalDateTime): Int {
      val o1Sec = o1.toEpochSecond(ZoneOffset.systemDefault().rules.getOffset(o1))
      val o2Sec = o2.toEpochSecond(ZoneOffset.systemDefault().rules.getOffset(o2))
      var ret = 0
      if (isGT) {
        if (o1Sec > o2Sec) ret = 1 else if (o1Sec < o2Sec) ret = -1
        return if (o1Sec > o2Sec) -1 else 1
      } else {
        if (o2Sec > o1Sec) ret = 1 else if (o2Sec < o1Sec) ret = -1
      }
      return ret
    }
  }
}