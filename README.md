# duplicate-files

## deprecated

Actual: [https://github.com/ezhov-da/duplicate](https://github.com/ezhov-da/duplicate)

```
RESULT_FILE=/d/duplicate.txt; \
rm -f $RESULT_FILE; \
find /d/redmi3s-20201221-work -type f -exec md5sum {} >> $RESULT_FILE \;; \
wc $RESULT_FILE
```