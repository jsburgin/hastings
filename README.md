# Hastings Designer Programming Language

Hastings is a dynamically typed language with functions as first class objects.
Written by Joshua Burgin for CS 403.

## Comments

To create a comment use the # symbol:

```
# this is a comment
var something = 12; # this is also a comment
```

## Defining variables

Variables can be defined by:

```
var myVariable = 12;
var myName = "Joshua";
var someValue = true;
var anotherValue = 2 + 2;
var oneMore = 16 ^ 3;
```

Strings are required to have double quotes around them.

## Updating variables

Updating variables is simple:

```
myVariable = "Some random string";
myName = "Sam";
oneMore = oneMore * 2;
```

## Operators

Hastings supports the following operators:

```
+ - * / ^
```

## Defining functions

Functions can be defined multiple ways as shown below.
Hastings supports anonymous functions.

```
func myCoolFunction(a, b) {
    return a + b;
}

var someOtherFunction = func(a) {
    return a * 2;
};

func returnsAFunction(a) {
    return func(b) {
        return a + b;
    };
}

func another(b) {
    print(b(12));
}

func fib(n) {
    if (n == 0) { return 0; }
    if (n == 1) { return 1; }

    return fib(n - 1) + fib(n - 2);
}
```

## Calling functions

Calling functions is simple:

```
myCoolFunction(2, 2);
someOtherFunction(20);

var returnedFunction = returnsAFunction(1);
returnedFunction(2);

another(func(b) {
    return b + 2;
});
```

## Arrays

Arrays can be created and accessed using bracket syntax:

```
var myArray = [1, 2, "Maybe a string", 4];
myArray[1]; # 2
myArray[4]; # Out of bounds error

var i = 0;
myArray[i]; # 1
myArray[i + 1]; # 2
```

## Printing to the console

Printing to the console allows multiple arguments.
Examples shown below:

```
print("Hello, world!"); # Hello, world!
print("One", "two"); # One two
```

## Conditionals

Hastings supports both && and ||.
Booleans have the values true and false.
Hastings supports >, <, >=, <=. ==, !=

Examples shown below:

```
if (0 == 0 && true) {
    print("Yep.");
}

if (1 == 0 || true) {
    print("Yep.");
}

if (true) {
    print("true");
} else {
    print("Not gonna happen.");
}

if (10 > 14) {
    print("Nope.");
} else if (10 < 14) {
    print("Yep.");
} else {
    print("Nope.");
}
```

## Objects & Dot operator

Objects examples shown below:

```
func DataStructure(value, left, right) {
    return this;
}

var myInstance = DataStructure(12, nil, nil);
print(myInstance.value); # 12
myInstance.left = DataStructure(14, nil, nil);

func Stack(head, size) {
    func push(value) {
        head = Node(value, head);
        size = size + 1;
    }

    func pop() {
        var returnValue = head;
        if (head != nil) {
            returnValue = head.value;
            head = head.next;
            size = size - 1;
        }
        return returnValue;
    }

    func peek() {
        if (head != nil) {
            print(head.value);
        }
    }

    return this;
}

var newStack = Stack(nil, 0);
newStack.push(15);
newStack.peek();
```

## Reading from files

Hastings allows file input using the built in read function.
This returns an array of values.

Example file:

```
a b c 1 2 3
```

```
var myData = read("./example.txt");
myData[0]; # "a"
myData[3]; # "1"
```

## Includes

To include another Hastings file use the built in include function.

```
include("./stack.has");
var myStack = Stack(nil, 0);
```
