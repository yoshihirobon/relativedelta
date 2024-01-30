using System;
 
class Relativedelta {
 
  public int? Year { get; set; } = null;
  public int? Month { get; set; } = null;
  public int? Day { get; set; } = null;
  public int? Hour { get; set; } = null;
  public int? Minute { get; set; } = null;
  public int? Second { get; set; } = null;
 
  private int years = 0;
  private int months = 0;
  private int days = 0;
  private int hours = 0;
  private int minutes = 0;
  private int seconds = 0;
  private int leapdays = 0;
 
  public Relativedelta(DateTime baseDay, DateTime day) {
 
    if (day < baseDay)
      // 日付が基準日より前の場合は例外とする
      throw new ArgumentException();
 
    var months = (day.Year - baseDay.Year) * 12 + (day.Month - baseDay.Month);
    this.SetMonths(months);
 
    var dtm = this.GetDTM(baseDay);
 
    Func<DateTime, DateTime, int> gt = (DateTime day, DateTime dtm) => day.CompareTo(dtm);
    Func<DateTime, DateTime, int> lt = (DateTime day, DateTime dtm) => dtm.CompareTo(day);
    var increment = 0;
    Func<DateTime, DateTime, int>? compare = null;
    if (day < baseDay) {
      compare = gt;
      increment = 1;
    } else {
      compare = lt;
      increment = -1;
    }
 
    while (compare(day, dtm) == 1) {
      months += increment;
      this.SetMonths(months);
      dtm = GetDTM(baseDay);
    }
 
    var delta = day - dtm;
    this.seconds = (int)delta.TotalSeconds;
 
    if (this.seconds > 59) {
      var s = (int)Math.Sign(this.seconds);
      int rem;
      int div = Math.DivRem(this.seconds * s, 60, out rem);
      this.seconds = rem * s;
      this.minutes += div * s;
    }
    if (this.minutes > 59) {
      var s = (int)Math.Sign(this.minutes);
      int rem;
      int div = Math.DivRem(this.minutes * s, 60, out rem);
      this.minutes = rem * s;
      this.hours += div * s;
    }
    if (this.hours > 23) {
      var s = (int)Math.Sign(this.hours);
      int rem;
      int div = Math.DivRem(this.hours * s, 24, out rem);
      this.hours = rem * s;
      this.days += div * s;
    }
    if (months > 11) {
      var s = (int)Math.Sign(this.months);
      int rem;
      int div = Math.DivRem(this.months * s, 12, out rem);
      this.months = rem * s;
      this.years += div * s;
    }
 
    this.Year = this.years;
    this.Month = this.months;
    this.Day = this.days;
    this.Hour = this.hours;
    this.Minute = this.minutes;
    this.Second = this.seconds;
  }
 
  private void SetMonths(int months) {
    this.months = months;
    if (this.months > 11) {
      var s = (int)Math.Sign(this.months);
      int rem;
      int div = Math.DivRem(this.months * s, 12, out rem);
      this.months = rem * s;
      this.years = div * s;
    } else {
      this.years = 0;
    }
  }
 
  private DateTime GetDTM(DateTime other) {
    var y = 0;
    if (this.Year != null) {
      y = (int)this.Year + this.years;
    } else if (other.Year > 0) {
      y = other.Year + this.years;
    }
    var m = this.Month != null ? this.Month : other.Month;
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
    var _d = this.Day != null ? this.Day : other.Day;
    var d = Math.Min(DateTime.DaysInMonth(y, (int)m), (int)_d);
 
    var repl = (
      year: y,
      month: (int)m,
      day: d,
      hour: (int)(this.Hour != null ? this.Hour : other.Hour),
      minute: (int)(this.Minute != null ? this.Minute : other.Minute),
      second: (int)(this.Second != null ? this.Second : other.Second)
    );
 
    var days = this.days;
    if (this.leapdays > 0 && m > 2 && DateTime.IsLeapYear(y)) {
      days += this.leapdays;
    }
 
    var ret = new DateTime(repl.year, repl.month, repl.day, repl.hour, repl.minute, repl.second);
    return ret.AddDays(days).AddHours(this.hours).AddMinutes(this.minutes).AddSeconds(this.seconds);
  }
}