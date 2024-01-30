import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
 
public class Relativedelta {
 
    private Integer year = null;
    private Integer month = null;
    private Integer day = null;
    private Integer hour = null;
    private Integer minute = null;
    private Integer second = null;
 
    private int years = 0;
    private int months = 0;
    private int days = 0;
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private int leapdays = 0;
    private HashMap<String, Integer> map = null;
 
    public Relativedelta(LocalDateTime baseDay, LocalDateTime day) {
 
        if (day.compareTo(baseDay) == -1) {
            // 日付が基準日より前の場合は例外とする
            throw new IllegalArgumentException();
        }
 
        var months = (day.getYear() - baseDay.getYear()) * 12 + (day.getMonthValue() - baseDay.getMonthValue());
        this.setMonths(months);
 
        var dtm = this.getDTM(baseDay);
 
        var baseSec = baseDay.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(baseDay));
        var daySec = day.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(day));
 
        MyCompare compare = null;
        var increment = 0;
        if (daySec < baseSec) {
            compare = new MyCompare(true);
            increment = 1;
        } else {
            compare = new MyCompare(false);
            increment = -1;
        }
 
        while (compare.compare(day, dtm) == 1) {
            months += increment;
            this.setMonths(months);
            dtm = getDTM(baseDay);
        }
        var duration = Duration.between(dtm, day);
        this.seconds = (int) duration.toSeconds();
 
        if (this.seconds > 59) {
            var s = (int) Math.signum(this.seconds);
            var div = Math.floorDiv(this.seconds * s, 60);
            var mod = (int) (this.seconds * s) % 60;
            this.seconds = mod * s;
            this.minutes += div * s;
        }
        if (this.minutes > 59) {
            var s = (int) Math.signum(this.minutes);
            var div = Math.floorDiv(this.minutes * s, 60);
            var mod = (int) (this.minutes * s) % 60;
            this.minutes = mod * s;
            this.hours += div * s;
        }
        if (this.hours > 23) {
            var s = (int) Math.signum(this.hours);
            var div = Math.floorDiv(this.hours * s, 24);
            var mod = (int) (this.hours * s) % 24;
            this.hours = mod * s;
            this.days += div * s;
        }
        if (months > 11) {
            var s = (int) Math.signum(this.months);
            var div = Math.floorDiv(this.months * s, 12);
            var mod = (int) (this.months * s) % 12;
            this.months = mod * s;
            this.years += div * s;
        }
 
        var map = new HashMap<String, Integer>();
        map.put("y", this.years);
        map.put("m", this.months);
        map.put("d", this.days);
        map.put("hh", this.hours);
        map.put("mm", this.minutes);
        map.put("ss", this.seconds);
 
        this.map = map;
    }
 
    private void setMonths(int months) {
        this.months = months;
        if (this.months > 11) {
            var s = (int) Math.signum(this.months);
            var div = Math.floorDiv(this.months * s, 12);
            var mod = (int) (this.months * s) % 12;
            this.months = mod * s;
            this.years = div * s;
        } else {
            this.years = 0;
        }
    }
 
    private LocalDateTime getDTM(LocalDateTime other) {
        var y = 0;
        if (this.year != null) {
            y = this.year + this.years;
        } else if (other.getYear() > 0) {
            y = other.getYear() + this.years;
        }
        var m = this.month != null ? this.month : other.getMonthValue();
        if (this.months > 0) {
            m += this.months;
            if (m > 12) {
                y += 1;
                m -= 12;
            } else if (m < 1) {
                y -= 1;
                m += 12;
            }
        }
        var _d = this.day != null ? this.day : other.getDayOfMonth();
        var d = Math.min(LocalDateTime.of(y, m, 1, 0, 0, 0).toLocalDate().lengthOfMonth(), _d);
 
        var repl = new HashMap<String, Integer>();
        repl.put("year", y);
        repl.put("month", m);
        repl.put("day", d);
        repl.put("hour", this.hour != null ? this.hour : other.getHour());
        repl.put("minute", this.minute != null ? this.minute : other.getMinute());
        repl.put("second", this.second != null ? this.second : other.getSecond());
 
        var days = this.days;
        var ldt = LocalDateTime.of(y, 1, 1, 0, 0, 0);
        if (this.leapdays > 0 && m > 2 && ldt.toLocalDate().isLeapYear()) {
            days += this.leapdays;
        }
        var ret = LocalDateTime.of(
                repl.get("year"),
                repl.get("month"),
                repl.get("day"),
                repl.get("hour"),
                repl.get("minute"),
                repl.get("second"));
 
        return ret.plusDays(days).plusHours(this.hours).plusMinutes(this.minutes).plusSeconds(this.seconds);
    }
 
    public HashMap<String, Integer> getMap() {
        return this.map;
    }
 
    private class MyCompare implements java.util.Comparator<LocalDateTime> {
 
        private boolean isGT;
 
        public MyCompare(boolean isGT) {
            this.isGT = isGT;
        }
 
        @Override
        public int compare(LocalDateTime o1, LocalDateTime o2) {
            var o1Sec = o1.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(o1));
            var o2Sec = o2.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(o2));
 
            var ret = 0;
            if (isGT) {
                if (o1Sec > o2Sec)
                    ret = 1;
                else if (o1Sec < o2Sec)
                    ret = -1;
                return o1Sec > o2Sec ? -1 : 1;
            } else {
                if (o2Sec > o1Sec)
                    ret = 1;
                else if (o2Sec < o1Sec)
                    ret = -1;
            }
            return ret;
        }
 
    }
}