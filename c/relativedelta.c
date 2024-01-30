#include "relativedelta.h"
 
DATES Relativedelta(struct tm baseDay, struct tm day) {
 
  struct tm dtm = {0};
  Compare compare = NULL;
  int increment = 0;
  int duration = 0;
  int s = 0;
  int div = 0;
  int mod = 0;
 
  DATES dates = {
      .year = -1,
      .month = -1,
      .day = -1,
      .hour = -1,
      .minute = -1,
      .second = -1,
      .years = 0,
      .months = 0,
      .days = 0,
      .hours = 0,
      .minutes = 0,
      .seconds = 0,
  };
 
  if (lt(&day, &baseDay) > 0) {
    errno = EINVAL;
    perror("Error");
    exit(1);
  }
 
  int months = ((day.tm_year + 1900) - (baseDay.tm_year + 1900)) * 12 +
               ((day.tm_mon + 1) - (baseDay.tm_mon + 1));
 
  setMonths(months, &dates);
 
  dtm = getDTM(baseDay, &dates);
 
  if (lt(&day, &baseDay) > 0) {
    compare = gt;
    increment = 1;
  } else {
    compare = lt;
    increment = -1;
  }
 
  while (compare(&day, &dtm) > 0) {
    months += increment;
    setMonths(months, &dates);
    dtm = getDTM(baseDay, &dates);
  }
 
  duration = difftime(mktime(&day), mktime(&dtm));
  dates.seconds = duration;
 
  if (dates.seconds > 59) {
    s = signbit(dates.seconds) == 0 ? 1 : 0;
    div = (dates.seconds * s) / 60;
    mod = (dates.seconds * s) % 60;
    dates.seconds = mod * s;
    dates.minutes += div * s;
  }
  if (dates.minutes > 59) {
    s = signbit(dates.minutes) == 0 ? 1 : 0;
    div = (dates.minutes * s) / 60;
    mod = (dates.minutes * s) % 60;
    dates.minutes = mod * s;
    dates.hours += div * s;
  }
  if (dates.hours > 23) {
    s = signbit(dates.hours) == 0 ? 1 : 0;
    div = (dates.hours * s) / 24;
    mod = (dates.hours * s) % 24;
    dates.hours = mod * s;
    dates.days += div * s;
  }
  if (dates.hours > 11) {
    s = signbit(dates.months) == 0 ? 1 : 0;
    div = (dates.months * s) / 12;
    mod = (dates.months * s) % 12;
    dates.months = mod * s;
    dates.years += div * s;
  }
 
  dates.year = dates.years;
  dates.month = dates.months;
  dates.day = dates.days;
  dates.hour = dates.hours;
  dates.minute = dates.minutes;
  dates.second = dates.seconds;
 
  return dates;
}
 
struct tm getDate(int y, int m, int d, int hour, int min, int sec) {
  struct tm ts = {0};
  time_t new_time;
  struct tm *new_ts = {0};
 
  ts.tm_year = y - 1900;
  ts.tm_mon = m - 1;
  ts.tm_mday = d;
  ts.tm_hour = hour;
  ts.tm_min = min;
  ts.tm_sec = sec;
 
  new_time = mktime(&ts);
  new_ts = localtime(&new_time);
  return *new_ts;
}
 
void setMonths(int months, LPDATES lpDates) {
  lpDates->months = months;
  if (lpDates->months > 11) {
    int s = signbit(lpDates->months) == 0 ? 1 : 0;
    int div = (lpDates->months * s) / 12;
    int mod = (lpDates->months * s) % 12;
    lpDates->months = mod * s;
    lpDates->years = div * s;
  } else {
    lpDates->years = 0;
  }
}
 
struct tm getDTM(struct tm other, LPDATES lpDates) {
  struct tm repl = {0};
  struct tm otherTm = {0};
  struct tm newTm = {0};
  int y = 0;
  if (lpDates->year != -1) {
    y = lpDates->year + lpDates->years;
  } else if (other.tm_year > 0) {
    y = other.tm_year + lpDates->years;
  }
  int m = lpDates->month != -1 ? lpDates->month : other.tm_mon + 1;
  if (lpDates->months > 0) {
    m += lpDates->months;
    if (m > 12) {
      y += 1;
      m -= 12;
    } else if (m < 1) {
      y -= 1;
      m += 12;
    }
  }
  int _d = lpDates->day != -1 ? lpDates->day : other.tm_mday;
  int d = MIN(lengthOfMonth(y, m), _d);
 
  repl.tm_year = y;
  repl.tm_mon = m;
  repl.tm_mday = d;
  repl.tm_hour = lpDates->hour;
  repl.tm_min = lpDates->minute;
  repl.tm_sec = lpDates->second;
 
  int days = lpDates->days;
  if (lpDates->leapdays > 0 && m > 2 && IsLeapYear(y) > 0) {
    days += lpDates->leapdays;
  }
 
  otherTm.tm_year = other.tm_year;
  otherTm.tm_mon = other.tm_mon;
  otherTm.tm_mday = other.tm_mday;
  otherTm.tm_hour = other.tm_hour;
  otherTm.tm_min = other.tm_min;
  otherTm.tm_sec = other.tm_sec;
 
  newTm.tm_year = repl.tm_year != -1 ? repl.tm_year : otherTm.tm_year;
  newTm.tm_mon = repl.tm_mon != -1 ? repl.tm_mon : otherTm.tm_mon;
  newTm.tm_mday = repl.tm_mday != -1 ? repl.tm_mday : otherTm.tm_mday;
  newTm.tm_hour = repl.tm_hour != -1 ? repl.tm_hour : otherTm.tm_hour;
  newTm.tm_min = repl.tm_min != -1 ? repl.tm_min : otherTm.tm_min;
  newTm.tm_sec = repl.tm_sec != -1 ? repl.tm_sec : otherTm.tm_sec;
 
  return getDate(newTm.tm_year + 1900, newTm.tm_mon, newTm.tm_mday + days,
                 newTm.tm_hour + lpDates->hours,
                 newTm.tm_min + lpDates->minutes,
                 newTm.tm_sec + lpDates->seconds);
}
 
int IsLeapYear(int year) {
  if (((year % 4 == 0) && (year % 100 != 0)) || year % 400 == 0) {
    return 1;
  } else {
    return 0;
  }
}
 
int lengthOfMonth(int y, int m) {
  int leap;
  int lmdays[] = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
  int mdays[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
 
  leap = IsLeapYear(y);
  if (leap == 1) {
    return lmdays[m - 1];
  } else {
    return mdays[m - 1];
  }
}
 
int gt(struct tm *_a, struct tm *_b) {
  time_t a = mktime(_a);
  time_t b = mktime(_b);
  if (a > b)
    return 1;
  else
    return -1;
}
 
int lt(struct tm *_a, struct tm *_b) {
  time_t a = mktime(_a);
  time_t b = mktime(_b);
  if (a < b)
    return 1;
  else
    return -1;
}
 
void printDate(struct tm *ts) {
  char buf[255] = {0};
  strftime(buf, sizeof(buf), "%Y-%m-%dT%H:%M:%S", ts);
  printf("%s\n", buf);
}
 
char *getPrintDate(struct tm *ts) {
  static char buf[255] = {0};
  memset(buf, 0, sizeof(buf));
  strftime(buf, sizeof(buf), "%Y-%m-%dT%H:%M:%S", ts);
  return buf;
}
 
void printElapsedDateTime(struct tm baseDay, struct tm day) {
  DATES dates = Relativedelta(baseDay, day);
  printf("%s: %d 年と %d ヶ月と %d 日と %d 時間と %d 分と %d 秒\n",
         getPrintDate(&day), dates.year, dates.month, dates.day, dates.hour,
         dates.minute, dates.second);
}
