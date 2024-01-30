import Foundation
 
class Relativedelta {
  var year: Int? = nil
  var month: Int? = nil
  var day: Int? = nil
  var hour: Int? = nil
  var minute: Int? = nil
  var second: Int? = nil
 
  var years = 0
  var months = 0
  var days = 0
  var hours = 0
  var minutes = 0
  var seconds = 0
  var leapdays = 0
 
  init(baseDay: Date, day: Date) throws {
 
    // if (day.compare(baseDay) == .orderedAscending) {
    if day < baseDay {
      throw NSError(domain: "Illegal Argument Exception", code: -1, userInfo: nil)
    }
 
    let cal = Calendar(identifier: .gregorian)
    var months =
      (cal.component(.year, from: day) - cal.component(.year, from: baseDay)) * 12
      + (cal.component(.month, from: day) - cal.component(.month, from: baseDay))
    self.setMonths(months: months)
 
    var dtm = self.getDTM(other: baseDay)
 
    let gt: (Date, Date) -> ComparisonResult = { $0.compare($1) }
    let lt: (Date, Date) -> ComparisonResult = { $1.compare($0) }
    var compare: (Date, Date) -> ComparisonResult
    var increment = 0
    // if (day.compare(baseDay) == .orderedAscending) {
    if day < baseDay {
      compare = gt
      increment = 1
    } else {
      compare = lt
      increment = -1
    }
 
    while compare(day, dtm) == .orderedDescending {
      months += increment
      self.setMonths(months: months)
      dtm = self.getDTM(other: baseDay)
    }
 
    // let interval = DateInterval(start: day, end: dtm)
    let interval = DateInterval(start: dtm, end: day)
    self.seconds = Int(interval.duration)
 
    if self.seconds > 59 {
      let s = self.sign(Double(self.seconds))
      let div = self.seconds * s / 60
      let mod = (self.seconds * s) % 60
      self.seconds = mod * s
      self.minutes += div * s
    }
    if self.minutes > 59 {
      let s = self.sign(Double(self.minutes))
      let div = self.minutes * s / 60
      let mod = (self.minutes * s) % 60
      self.minutes = mod * s
      self.hours += div * s
    }
    if self.hours > 23 {
      let s = self.sign(Double(self.hours))
      let div = self.hours * s / 24
      let mod = (self.hours * s) % 24
      self.hours = mod * s
      self.days += div * s
    }
    if self.months > 11 {
      let s = self.sign(Double(self.months))
      let div = self.months * s / 12
      let mod = (self.months * s) % 12
      self.months = mod * s
      self.years += div * s
    }
 
    self.year = self.years
    self.month = self.months
    self.day = self.days
    self.hour = self.hours
    self.minute = self.minutes
    self.second = self.seconds
  }
 
  private func setMonths(months: Int) {
    self.months = months
    if self.months > 11 {
      let s = self.sign(Double(self.months))
      let div = self.months * s / 12
      let mod = (self.months * s) % 12
      self.months = mod * s
      self.years = div * s
    } else {
      self.years = 0
    }
  }
 
  private func getDTM(other: Date) -> Date {
    let cal = Calendar(identifier: .gregorian)
    var y = cal.component(.year, from: other) + self.years
    if let _y = self.year {
      y = _y + self.years
    }
    var m = self.month != nil ? self.month! : cal.component(.month, from: other)
    if self.months > 0 {
      m = m + self.months
      if m > 12 {
        y += 1
        m -= 12
      } else if m < 1 {
        y -= 1
        m += 12
      }
    }
    let _d = self.day != nil ? self.day! : cal.component(.day, from: other)
    var comps = DateComponents()
    comps.year = y
    comps.month = m
    var dayInMonth = 0
    if let date = cal.date(from: comps) {
      if let rng = cal.range(of: .day, in: .month, for: date) {
        dayInMonth = rng.upperBound - 1
      }
    }
 
    let d = min(dayInMonth, _d)
 
    let repl = (
      year: y,
      month: m,
      day: d,
      hour: self.hour != nil ? self.hour! : cal.component(.hour, from: other),
      minute: self.minute != nil ? self.minute! : cal.component(.minute, from: other),
      second: self.second != nil ? self.second! : cal.component(.second, from: other)
    )
    var days = self.days
    if self.leapdays > 0 && m > 2 && self.isYearLeapYear(y) {
      days += self.leapdays
    }
    //let ret = cal.setY(repl.year, M:repl.month, d:repl.day, h:repl.hour, m:repl.minute, s:repl.second)!
    let ret = cal.setY(repl.year, repl.month, repl.day, repl.hour, repl.minute, repl.second)!
    comps = DateComponents()
    comps.day = days
    comps.hour = self.hours
    comps.minute = self.minutes
    comps.second = self.seconds
    return cal.date(byAdding: comps, to: ret)!
  }
 
  private func sign(_ x: Double) -> Int {
    return (x < 0) ? -1 : (x > 0) ? +1 : 0
  }
 
  private func isYearLeapYear(_ year: Int) -> Bool {
    var result = false
 
    if year % 4 == 0 {
      if year % 100 == 0 {
        result = year % 400 == 0
      } else {
        return true
      }
    }
 
    return result
  }
}