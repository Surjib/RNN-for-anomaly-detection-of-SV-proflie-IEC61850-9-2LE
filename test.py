import datetime as dt

# Create two datetime objects
date1 = dt.datetime(2023, 3, 14, 15, 30, 45, 123456)
date2 = dt.datetime(2023, 3, 14, 15, 35, 15, 789123)

# Calculate the interval between the two datetimes
interval = date2 - date1

# Print the interval
print(interval)