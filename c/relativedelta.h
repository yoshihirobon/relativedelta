#ifndef RELATIVEDELTA_H
#define RELATIVEDELTA_H
 
#define _XOPEN_SOURCE
#include <math.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/param.h>
#include <time.h>
#include <errno.h>
 
typedef struct _DATES {
  int year;
  int month;
  int day;
  int hour;
  int minute;
  int second;
 
  int years;
  int months;
  int days;
  int hours;
  int minutes;
  int seconds;
  int leapdays;
} DATES, *LPDATES;
 
DATES Relativedelta(struct tm, struct tm);
struct tm getDate(int, int, int, int, int, int);
void setMonths(int, LPDATES);
struct tm getDTM(struct tm, LPDATES);
int IsLeapYear(int);
int lengthOfMonth(int, int);
void printDate(struct tm *);
int gt(struct tm *, struct tm *);
int lt(struct tm *, struct tm *);
char *getPrintDate(struct tm *);
void printElapsedDateTime(struct tm, struct tm);
typedef int (*Compare)(struct tm *, struct tm *);
 
#endif