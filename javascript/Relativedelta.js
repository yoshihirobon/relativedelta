class Relativedelta {
  constructor(baseDay, day) {
    if (day < baseDay) {
      throw new Error("IllegalArgumentException");
    }
 
    this.year = null;
    this.month = null;
    this.day = null;
    this.hour = null;
    this.minute = null;
    this.second = null;
 
    this.years = 0;
    this.months = 0;
    this.days = 0;
    this.hours = 0;
    this.minutes = 0;
    this.seconds = 0;
    this.leapdays = 0;
 
    let months =
      (day.getFullYear() - baseDay.getFullYear()) * 12 +
      ((day.getMonth() + 1) - (baseDay.getMonth() + 1));
 
    this.setMonths(months);
 
    let dtm = this.getDTM(baseDay);
    // console.log(dtm);
 
    let gt = (a, b) => a > b;
    let lt = (a, b) => a < b;
    let compare = null;
    let increment = 0;
    if (day < baseDay) {
      compare = gt;
      increment = 1;
    } else {
      compare = lt;
      increment = -1;
    }
 
    while (compare(day, dtm)) {
      months += increment;
      this.setMonths(months);
      dtm = this.getDTM(baseDay);
    }
 
    var duration = this.getDuration(day - dtm);
    this.seconds = duration["total_seconds"];
 
    if (this.seconds > 59) {
      var s = Math.sign(this.seconds);
      var div = parseInt((this.seconds * s) / 60);
      var mod = parseInt((this.seconds * s) % 60);
      this.seconds = mod * s;
      this.minutes += div * s;
    }
    if (this.minutes > 59) {
      var s = Math.sign(this.minutes);
      var div = parseInt((this.minutes * s) / 60);
      var mod = parseInt((this.minutes * s) % 60);
      this.minutes = mod * s;
      this.hours += div * s;
    }
    if (this.hours > 23) {
      var s = Math.sign(this.hours);
      var div = parseInt((this.hours * s) / 24);
      var mod = parseInt((this.hours * s) % 24);
      this.hours = mod * s;
      this.days += div * s;
    }
    if (months > 11) {
      var s = Math.sign(this.months);
      var div = parseInt((this.months * s) / 12);
      var mod = parseInt((this.months * s) % 12);
      this.months = mod * s;
      this.years += div * s;
    }
 
    this.year = this.years;
    this.month = this.months;
    this.day = this.days;
    this.hour = this.hours;
    this.minute = this.minutes;
    this.second = this.seconds;
  }
 
  setMonths(months) {
    this.months = months;
    if (this.months > 11) {
      var s = Math.sign(this.months);
      var div = parseInt((this.months * s) / 12);
      var mod = parseInt((this.months * s) % 12);
      this.months = mod * s;
      this.years = div * s;
    } else {
      this.years = 0;
    }
  }
 
  getDTM(other) {
    let y = 0;
    if (this.year != null) {
      y = this.year + this.years;
    } else if (other.getFullYear() > 0) {
      y = other.getFullYear() + this.years;
    }
    let m = this.month != null ? this.month : other.getMonth() + 1;
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
    let _d = this.day != null ? this.day : other.getDate();
    let d = Math.min(this.lengthOfMonth(y, m), _d);
 
    let repl = {
      "year": y,
      "month": m,
      "day": d
    }
 
    for (const attr of ["hour", "minute", "second"]) {
      if (this[attr] != null) {
        repl[attr] = this[attr];
      }
    }
 
    var days = this.days;
    if (this.leapdays > 0 && m > 2 && isLeap(y)) {
      days += this.leapdays;
    }
 
    let otherObj = {
      "year": other.getFullYear(),
      "month": other.getMonth() + 1,
      "day": other.getDate(),
      "hour": other.getHours(),
      "minute": other.getMinutes(),
      "second": other.getSeconds()
    }
 
    let newObj = { ...otherObj, ...repl };
    let newDate = new Date(
      newObj["year"],
      newObj["month"] - 1,
      newObj["day"] + days,
      newObj["hour"] + this.hours,
      newObj["minute"] + this.minutes,
      newObj["second"] + this.seconds
    );
 
    return newDate;
  }
 
  lengthOfMonth(y, m) {
    let date = new Date(y, m, 0);
    return date.getDate();
  }
 
  isLeap(year) {
    if (year % 4 === 0) {
      if (year % 100 === 0) {
        return year % 400 === 0;
      } else {
        return true;
      }
    }
    return false;
  }
 
  getDuration(duration) {
    let totalMilliseconds = duration;
    let totalSeconds = totalMilliseconds / 1000;
    let totalMinutes = totalSeconds / 60;
    let totalHours = totalMinutes / 60;
    let totalDays = totalHours / 24;
 
    let days = totalDays > 0 ? parseInt(Math.floor(totalDays)) : parseInt(Math.ceil(totalDays))
    let hours = parseInt(totalHours) - (parseInt(totalDays) * 24)
    let minutes = parseInt(parseInt(totalMinutes) - (parseInt(totalHours) * 60))
    let seconds = parseInt(parseInt(totalSeconds) - (parseInt(totalMinutes) * 60))
    let total_seconds = totalSeconds;
 
    return { "days": days, "hours": hours, "minutes": minutes, "seconds": seconds, "total_seconds": total_seconds };
  }
}
 
module.exports = Relativedelta;