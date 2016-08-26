all: group compiler

group:
	echo "Kyle Rakos krakos"
	echo "Brian Rieder brieder"

compiler: helloworld

helloworld: helloworld.c
	$(CC) -o $@ $<

clean:
	rm helloworld
