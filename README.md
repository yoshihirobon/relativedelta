# relativedelta

## Java

```Java
var baseDay = LocalDateTime.of(2012, 1, 1, 0, 0, 0);
System.out.printf("%s: <-- 基準日\n", baseDay.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

var day = LocalDateTime.of(2013, 1, 30, 23, 59, 59);
var res = new Relativedelta(baseDay, day);
var map = res.getMap();

var s = String.format("%s: %d 年と %d ヶ月と %d 日と %d 時間と %d 分と %d 秒",
                day.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                map.get("y"), map.get("m"), map.get("d"),
                map.get("hh"), map.get("mm"), map.get("ss"));
System.out.println(s);
```

## Kotlin

```kotlin
val baseDay = LocalDateTime.of(2012, 1, 1, 0, 0, 0)
System.out.printf("%s: <-- 基準日\n", baseDay.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))

val day = LocalDateTime.of(2013, 1, 30, 23, 59, 59)
val res = Relativedelta(baseDay, day)
val map = res.getMap()!!

val s = String.format(
  "%s: %d 年と %d ヶ月と %d 日と %d 時間と %d 分と %d 秒",
  day.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
  map["y"], map["m"], map["d"],
  map["hh"], map["mm"], map["ss"]
)
println(s)
```

## C#

```C#
DateTime baseDay = new DateTime(2012, 1, 1, 0, 0, 0);
Console.WriteLine("{0}: <-- 基準日", baseDay.ToString("s"));

var day = new DateTime(2013, 1, 30, 23, 59, 59);
var res = new Relativedelta(baseDay, day);
var s = String.Format("{0}: {1} 年と {2} ヶ月と {3} 日と {4} 時間と {5} 分と {6} 秒",
  day.ToString("s"), res.Year, res.Month, res.Day, res.Hour, res.Minute, res.Second);
Console.WriteLine(s);
```

## Swift

```Swift
import Foundation

let cal = Calendar(identifier: .gregorian)
let df = DateFormatter()
df.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"

let baseDate = cal.setY(2012, 1, 1, 0, 0, 0)!
print("\(df.string(from: baseDate)): <-- 基準日")

let day = cal.setY(2013, 1, 30, 23, 59, 59)!
let res = try Relativedelta(baseDay: baseDate, day: day)
print("\(df.string(from: day)): \(res.year!) 年と \(res.month!) ヶ月と \(res.day!) 日と \(res.hour!) 時間と \(res.minute!) 分と \(res.second!) 秒")
```

## JavaScript

```JavaScript
const Relativedelta = require('./Relativedelta');

function fmtDate(day) {
  let s1 = day.toLocaleString('ja-JP', {
    timeZone: 'Asia/Tokyo', year: 'numeric', month: '2-digit', day: '2-digit',
  });
  let s2 = day.toLocaleString('ja-JP', {
    timeZone: 'Asia/Tokyo', hour: '2-digit', minute: '2-digit', second: '2-digit',
  });
  return s1.replaceAll("/", "-") + "T" + s2;
}

let baseDay = new Date(2012, 1 - 1, 1, 0, 0, 0);
console.log(`${fmtDate(baseDay)}: <-- 基準日`);

let day = new Date(2013, 1 - 1, 30, 23, 59, 59);
let res = new Relativedelta(baseDay, day);
console.log(
  `${fmtDate(day)}: ${res.year} 年と ${res.month} ヶ月と ${res.day} 日と ${res.hour} 時間と ${res.minute} 分と ${res.second} 秒`
);
```

## C

```C
#include "relativedelta.h"

int main(void) {

  struct tm baseDay = getDate(2012, 1, 1, 0, 0, 0);
  printf("%s: <-- 基準日\n", getPrintDate(&baseDay));

  struct tm day = getDate(2013, 1, 30, 23, 59, 59);
  DATES dates = Relativedelta(baseDay, day);
  printf("%s: %d 年と %d ヶ月と %d 日と %d 時間と %d 分と %d 秒\n",
         getPrintDate(&day), dates.year, dates.month, dates.day, dates.hour,
         dates.minute, dates.second);

  return 0;
}
```