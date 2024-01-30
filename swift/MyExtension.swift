import Foundation
 
extension Calendar {
  func setY(
    _ y: Int = 0,
    _ M: Int = 0,
    _ d: Int = 0,
    _ h: Int = 0,
    _ m: Int = 0,
    _ s: Int = 0,
    _ z: TimeZone = TimeZone.current
  ) -> Date? {
 
    var comps = DateComponents()
    comps.year = y
    comps.month = M
    comps.day = d
    comps.hour = h
    comps.minute = m
    comps.second = s
    comps.nanosecond = 0
    comps.timeZone = z
 
    return self.date(from: comps)!
  }
}