# DisplayList
## Notes
- Attempts to connect to https://fetch-hiring.s3.amazonaws.com/hiring.json 5 times before giving up.
	- If it cannot connect, the error will be logged and the list will not be displayed. 
- Uses a backend in memory database, which is then queried.
- The sort order for item name is ALPHANUMERIC.
