#include <iostream>
#include <string>
#include <stdio.h>
#include <list>
#include <vector>
#include <stdexcept>
#include <cassert>

#define DEBUG 1
#define TEST 0

#if !DEBUG
#define NDEBUG
#endif

void debug(std::string str)
{
#if DEBUG
	printf("%s\n", str.c_str());
#endif
}

void error(std::string error_message, int error_code)
{
	fprintf(stderr, "%s", error_message.c_str());
	exit(error_code);
}

void error(std::string error_message)
{
	error(error_message, 0);
}
typedef enum
{
	Function,
	Integer,
	Float,
	List,
	Atom,
	Undefined,
	Boolean
} LiteralType;
/*
typedef struct
{
	int param_numbers;
	void *ptr;
} FunctionMeta;
*/
typedef struct Literal
{
	LiteralType type;
	void *ptr = nullptr;
	Literal(int value) : type(Integer), ptr(new int(value))
	{
		ptr = new int(value);
		int ptr_val = *(int *)ptr;
	}
	Literal(double value) : type(Float), ptr(new double(value)) {}
	Literal(size_t value) : type(Atom), ptr(new size_t(value)) {}
	Literal(bool value) : type(Boolean), ptr(new bool(value)) {}
	//Literal(std::vector<Literal> value) : type(List), ptr(new std::list<Literal>(value.begin(), value.end())) {}
	Literal(std::list<Literal> value) : type(List), ptr(new std::list<Literal>(value))
	{
		debug("list constructor");
	}
	//Literal(std::string value) : type(String), ptr(new std::string(value)) {}
	//Literal(FunctionMeta value) : type(Function), ptr(new FunctionMeta(value)) {}
	Literal() : type(Undefined) {}
	/*		Literal(Literal&& a)
	{
#if DEBUG 
		std::cout << "move called" << std::endl;
#endif
		
		this->type = a.type;
		this->ptr = a.ptr;
		a.type = Undefined;
		a.ptr = nullptr;
	} */
	Literal(const Literal &a)
	{
		if (*this == a)
			return;
		debug("copy called");
		this->deleteLiteral();
		this->type = a.type;
		switch (a.type)
		{
		case Integer:
			this->ptr = new int(*(int *)a.ptr);
			break;
		case Float:
			this->ptr = new double(*(double *)a.ptr);
			break;
		case List:
			this->ptr = new std::list<Literal>(*(std::list<Literal> *)a.ptr);
			break;
		case Function:
			this->ptr = a.ptr;
			break;
		case Atom:
			this->ptr = new size_t(*(size_t *)a.ptr);
			break;
		case Boolean:
			this->ptr = new bool(*(bool *)a.ptr);
			break;
		case Undefined:
			break;
		}
	}
	int getInt() const
	{
		debug("getInt");
		if (type != Integer)
		{
			error("Type error.");
		}

		int result = *(int *)this->ptr;
		return result;
	}

	double getFloat() const
	{
		debug("getFloat");
		if (type != Float)
			error("Type error.");
		double result = *(double *)this->ptr;
		return result;
	}

	std::list<Literal> getList() const
	{
		debug("getList");
		if (type != List)
			error("Type error: not a list.");
		std::list<Literal> result = *(std::list<Literal> *)this->ptr;
		return result;
	}

	size_t getAtom() const
	{
		debug("getAtom");
		if (type != Atom)
			error("Type error.");
		size_t result = *(size_t *)this->ptr;
		return result;
	}

	bool getBoolean() const
	{
		debug("getBoolean");
		if (type != Boolean)
			error("Type error.");
		bool result = *(bool *)this->ptr;
		return result;
	}

	bool operator==(const Literal &rhs) const
	{
		debug("operator==");
		if (rhs.type != type)
			return false;

		switch (type)
		{
		case Integer:
			return getInt() == rhs.getInt();
		case Float:
			return getFloat() == rhs.getFloat();
		case List:
			return getList() == rhs.getList();
		case Function:
			return ptr == rhs.ptr;
		case Atom:
			return getAtom() == rhs.getAtom();
		case Boolean:
			return getBoolean() == rhs.getBoolean();
		case Undefined:
			error("Comparison between undefined variables is impossible.");
		}

		return false;
	}
	bool operator<(const Literal &rhs) const
	{
		debug("operator<");
		// TODO: support comparison between different types.
		if (rhs.type != type)
			return false;

		switch (type)
		{
		case Integer:
			return getInt() < rhs.getInt();
		case Float:
			return getFloat() < rhs.getFloat();
		case List:
			return getList() < rhs.getList();
		case Function:
			error("Comparison between functions is not supported.");
		case Atom:
			error("Comparison between atoms is not supported.");
		case Boolean:
			return getBoolean() < rhs.getBoolean();
		case Undefined:
			error("Comparison between undefined variables is impossible.");
		}

		return true;
	}

	bool operator!=(const Literal &rhs) const
	{
		debug("operator!=");
		return !((*this) == rhs);
	}
	bool operator<=(const Literal &rhs) const
	{
		debug("operator<=");
		if (rhs.type != type)
			return false;

		return (*this < rhs) || (*this == rhs);
	}

	bool operator>(const Literal &rhs) const
	{
		debug("operator>");
		if (rhs.type != type)
			return false;

		return rhs < *this;
	}

	bool operator>=(const Literal &rhs) const
	{
		debug("operator>=");
		if (rhs.type != type)
			return false;

		return rhs <= *this;
	}

	Literal listHead()
	{
		debug("listHead");
		if ((type != List) || !ptr)
		{
			if (!ptr)
				// std::cout << "something wrong";
				error("head error type");
		}
		std::list<Literal> *ptrl = (std::list<Literal> *)ptr;
		if (ptrl->empty())
		{
			error("head error");
		}
		return Literal(*ptrl->begin());
	}

	   Literal listTail()
	{
		debug("listTail");
		if ((type != List) || !ptr)
		{

			error("tail error type");
		}
		std::list<Literal> *ptrl = (std::list<Literal> *)ptr;
		if (ptrl->empty())
		{
			error("tail error");
		}
		std::list<Literal> result(next(ptrl->begin()), ptrl->end());
		return result;
	}
	void deleteLiteral()
	{
		debug("deleteLiteral");
		type = Undefined;
		if (ptr)
		{
			switch (type)
			{
			case List:
				delete (std::list<Literal> *)ptr;
				break;
			case Function:
				break;
			case Undefined:
				break;
			case Atom:
				delete (size_t *)ptr;
				break;
			case Float:
				delete (double *)ptr;
				break;
			case Integer:
				delete (int *)ptr;
				break;
			case Boolean:
				delete (bool *)ptr;
				break;
			}
		}
		ptr = nullptr;
	}
	~Literal()
	{
		debug("destructor");
		deleteLiteral();
	}

	bool isNumber() const
	{
		debug("isNumber");
		return type == Integer || type == Float;
	}

	Literal operator+(Literal const &a)
	{
		debug("operator+");
		if (!(isNumber() && a.isNumber()))
			error("Invalid sum between incompatible terms.");

		if (type == Integer && a.type == Integer)
		{
			return Literal(getInt() + a.getInt());
		}
		else if (type == Float && a.type == Float)
		{
			return Literal(getFloat() + a.getFloat());
		}
		else if (type == Integer)
		{
			return Literal(((double)getInt()) + a.getFloat());
		}
		// else if(type == Float)
		return Literal(((double)a.getInt()) + getFloat());
	}

	Literal operator-(Literal const &a)
	{
		debug("operator-");
		return (*this + a.negative());
	}

	Literal operator*(Literal const &a)
	{
		debug("operator*");
		if (!(isNumber() && a.isNumber()))
			error("Invalid product between incompatible terms.");

		if (type == Integer && a.type == Integer)
		{
			return Literal(getInt() * a.getInt());
		}
		else if (type == Float && a.type == Float)
		{
			return Literal(getFloat() * a.getFloat());
		}
		else if (type == Integer)
		{
			return Literal(((double)getInt()) * a.getFloat());
		}
		//else if(type == Float)
		return Literal(((double)a.getInt()) * getFloat());
	}

	Literal operator/(Literal const &a)
	{
		debug("operator/");
		if (!(isNumber() && a.isNumber()))
			error("Invalid division between incompatible terms.");

		if (type == Integer && a.type == Integer)
		{
			return Literal(getInt() / a.getInt());
		}
		else if (type == Float && a.type == Float)
		{
			return Literal(getFloat() / a.getFloat());
		}
		else if (type == Integer)
		{
			return Literal(getInt() / a.getFloat());
		}
		// else if(type == Float)
		return Literal(getFloat() / a.getInt());
	}

	Literal operator%(Literal const &a)
	{
		debug("operator%");
		if (!(type != Integer && a.type != Integer))
			error("Invalid arithmetic expression, modulo accepts only integers.");

		if (type == Integer && a.type == Integer)
		{
			return Literal(getInt() % a.getInt());
		}
		else if (type == Float && a.type == Float)
		{
			return Literal((int)getFloat() % (int)a.getFloat());
		}
		else if (type == Integer)
		{
			return Literal(getInt() % (int)a.getFloat());
		}
		// else if(type == Float)
		return Literal((int)getFloat() % a.getInt());

		error("Invalid modulo between incompatible terms.");
	}

	bool exequal(const Literal &rhs)
	{
		debug("exact equals");
		return *this == rhs;
	}

	bool equal(const Literal &rhs)
	{
		debug("equals");
		if (this->exequal(rhs))
		{
			return true;
		}

		if (rhs.type == Integer && type == Float)
		{
			return ((double)rhs.getInt()) == this->getFloat();
		}
		else if (rhs.type == Float && type == Integer)
		{
			return ((double)this->getInt()) == rhs.getFloat();
		}

		return false;
	}

	Literal negative() const
	{
		debug("negative");
		if (type == Integer)
		{
			return -getInt();
		}
		else if (type == Float)
		{
			return -getFloat();
		}

		error("Cannot change sign to a non-numerical value.");
		return 0;
	}

	std::string getString()
	{
		if (isNumber())
		{
			return std::to_string(type == Integer ? getInt() : getFloat());
		}
		else
		{
			return "";
		}
	}

} Literal;

std::string literalType(Literal &l)
{
	switch (l.type)
	{
	case Integer:
		return "Integer";
	case Float:
		return "Float";
	case Atom:
		return "Integer";
	case Function:
		return "Function";
	case List:
		return "List";
	case Undefined:
		return "Undefined";
	case Boolean:
		return "Boolean";
	}
	return "";
}

/*Literal start()
{
	Literal Num;
	if (Num.type == Undefined)
	{
		Num = Literal(42);
	}
	Literal Pi;
	if (Pi.type == Undefined)
	{
		Pi = Literal(3.14159);
	}
	Literal Hello;
	if (Hello.type == Undefined)
	{
		Hello = Literal((size_t)0);
	}
	Literal OtherNode;
	if (OtherNode.type == Undefined)
	{
		OtherNode = Literal((size_t)1);
	}
	Literal StrangeString;
	if (StrangeString.type == Undefined)
	{
		StrangeString = Literal("example@n\"ode");
	}
	Literal Listing;
	if (Listing.type == Undefined)
	{
		Listing = Literal({Literal(1), Literal(2), Literal(3), Literal(4)});
	}
	return 0;
}

Literal start(Literal arg)
{
	if (arg == Literal(2))
	{ // fun (line 8) 
		Literal Num;
		if (Num.type == Undefined)
		{
			Num = Literal(42);
		}
		Literal Pi;
		if (Pi.type == Undefined)
		{
			Pi = Literal(3.14159);
		}
		Literal Hello;
		if (Hello.type == Undefined)
		{
			Hello = Literal((size_t)0);
		}
		if (Num != Literal(42))
		{
			error("Bad Match");
		}
		Literal OtherUndef;
		if (OtherUndef.type == Undefined)
		{
			OtherUndef = Num;
		}
		Literal NumUndef;
		if (NumUndef.type == Undefined)
		{
			NumUndef = OtherUndef;
		}
		Literal OtherNode;
		if (OtherNode.type == Undefined)
		{
			OtherNode = Literal((size_t)1);
		}
		Literal StrangeString;
		if (StrangeString.type == Undefined)
		{
			StrangeString = Literal("example@n\"ode");
		}
		Literal Listing;
		if (Listing.type == Undefined)
		{
			Listing = Literal({Literal(1), Literal(2), Literal(3), Literal(4)});
		}
	}
	else if (arg.type != Undefined)
	{ // fun (line 16) 
		Literal N = arg;
		Literal Lol;
		if (Lol.type == Undefined)
		{
			Lol = N;
		}
	}

	return 0;
}
*/

/*
Es1
Pattern = Expr1
[First, Second, Third | Tail] = [1,2, [1,2], 4,5, [4,3]]
First =  [1,2, [1,2], 4,5, [4,3]].at(0); // 1
Second = [1,2, [1,2], 4,5, [4,3]].at(1); // 2
Third =  [1,2, [1,2], 4,5, [4,3]].at(2); // [1,2]
Tail =  [1,2, [1,2], 4,5, [4,3]].tail(); // [4, 5, [4,3]]

Pattern = List | >=3 | Tail


Es2
Pattern = Expr1
[First, 2, [FirstSecond, SecondSecond], Third | Tail] = [1, 2, [1,2], 4,5, [4,3]]
First =  [1,2, [1,2], 4,5, [4,3]].at(0); // 1
FirstSecond = [1,2, [1,2], 4,5, [4,3]].at(2).at(0); // 1
SecondSecond = [1,2, [1,2], 4,5, [4,3]].at(2).at(1); // 2
Third =  [1,2, [1,2], 4,5, [4,3]].at(2); // 4
Tail =  [1,2, [1,2], 4,5, [4,3]].tail(); // [4, 5, [4,3]]
Pattern = List | >=3 | Tai

*/

/*
Literal fib(Literal N);

Literal fib(Literal N) {
	
	int n = *(int*)N.ptr;
	// std::cout << "fib(" << n << ")" << std::endl;
	if(*(int*)N.ptr == 0) {
		// std::cout << "end fib(" << n << ")" << std::endl;
	return Literal(0);
	} else if(*(int*)N.ptr == 1) {
		// std::cout << "end fib(" << n << ")" << std::endl;
	return  Literal(1);
	} else if(*(int*)N.ptr > 1) {
	Literal tmp1 = fib(Literal(n - 1));// std::cout << "getint 1 " << fib(Literal(n - 1)).getInt() << std::endl;

	// std::cout << "getint 1 " << tmp1.getInt() << std::endl;
	Literal tmp2 = fib(Literal(n - 2));
	// std::cout << "getint 2 " << tmp2.getInt() << std::endl;
	Literal result = tmp1 + tmp2;
	// std::cout << "getint r " << result.getInt() << std::endl;
	int rv = *(int*)result.ptr;
	// std::cout << "sum(fib(" << (n - 1) << "), fib(" << (n - 2) << ")) = " << rv << std::endl;
	// std::cout << "end fib(" << n << ")" << std::endl;
	return result;
	}
	

	//return Literal(0);
}



sum(L) -> sum(L, 0).
sum([], N)	-> N;
sum([H|T], N) -> sum(T, H+N).


Literal sum(Literal L, Literal N);
Literal sum(Literal L);
Literal sum(Literal L) {
	return sum(L, Literal(0));
}
Literal sum(Literal L, Literal N) {

	if(((std::list<Literal>*)L.ptr)->empty())
	  return Literal(N);
	if(L.type == List || L.type == String)
	return sum( L.listTail(),L.listHead() + Literal(N));
}
*/
void add()
{
	Literal one(1);
	Literal three(3);
	Literal four = three + one;
#if TEST
	assert(four.getInt() == (1 + 3));
#endif
	//std::cout << "Four: " << four.getInt() << std::endl;
}

void addpar(Literal one, Literal three)
{
	Literal four = three + one;
#if TEST
	assert(four.getInt() == (1 + 3));
#endif
	//std::cout << "Four: " << four.getInt() << std::endl;
}

Literal addparret(Literal one, Literal three)
{
	Literal four = three + one;
	Literal ret = four + 5;
	ret = ret - 5;
#if TEST
	assert(four.getInt() == (1 + 3));
#endif
	return ret;
	//std::cout << "Four: " << four.getInt() << std::endl;
}

void addMixed()
{
	Literal one(1);
	Literal pi(3.141592);
	Literal oneppi = pi + one;
#if TEST
	assert(oneppi.getFloat() == ((double)3.141592 + 1));
#endif
	//std::cout << "One Plus Pi: " << oneppi.getFloat() << std::endl;
}
void declare1() {
	Literal l(1), l2(2), l5(5);
}
void declare1prime() {
	Literal l(1), l2(2), l5(5);
	Literal s = l2 + l;
}
void declare1primep() {
	Literal l(1), l2(2), l5(5);
	Literal s;
	s = l2 + l;
}
void declare2() {
	Literal l(1), m(2);
	l.getInt();
	m.getFloat();
}
void normaladd()
{
	int a = 4, b = 5;
	int c = a + b;
	printf("%d\n", c);
}
void sub()
{
	Literal one(1);
	Literal three(3);
	Literal two = three - one;
#if TEST
	assert(two.getInt() == (3 - 1));
#endif
	//std::cout << "Two: " << two.getInt() << std::endl;
}
/*void str() {
	Literal one("one");
	Literal oneprime({'o', 'n', 'e'});
	//assert(one.getList() == oneprime.getList());
	debug("str");
	for(auto c: oneprime.getList()) {
		std::cout << c.getString();
	}
	std::cout << std::endl;
} */
Literal sum(Literal L, Literal N);
Literal sum(Literal L);
Literal sum(Literal L) {
	return sum(L, 0);
}
Literal sum(Literal L, Literal N) {
	
	if(L == Literal(std::list<Literal>()))
		return N;
	if(L.type == List)
		return sum(L.listTail(), L.listHead() + N);
	error("bad matching");
}
int main()
{

	//std::vector<Literal> lv = {Literal(1), Literal(3), Literal(std::string("ciao")), Literal((double) 1.2)};
	//Literal mylist(std::list(lv.begin(), lv.end()));
	/*Literal mylistN({Literal(1), Literal(3), Literal(4)});
		Literal myOtherList({Literal(1), Literal(3), Literal(4)});
		Literal myAList({Literal(1)});
		Literal pi = Literal(1);
		Literal ni = pi.negative();
		std::cout << "pi" <<  literalType(pi) << std::endl;
		std::cout << "ni" << literalType(ni) << std::endl;
		std::cout << ni.getInt() << std::endl;

		assert(mylistN == myOtherList);
		if (mylistN != myOtherList)
		{
			std::cout << "first are not equal" << std::endl;
		}
		else
		{
			std::cout << "first are equal" << std::endl;
		}
		if (myAList != myOtherList)
		{
			std::cout << "second are not equal" << std::endl;
		}
		else
		{
			std::cout << "second are equal" << std::endl;
		}
		
		Literal ra = (Literal(1) + Literal(2.5))/ 1.1;
		std::cout << "ra: " << ra.getFloat() << std::endl;
*/
	//Literal results = sum(mylistN);
	//int vals = *(int*)results.ptr;
	// std::cout << "Hello World!\n" << "Sum: " << vals;
	//Literal result = fib(Literal(10));
	//int val = *(int*)result.ptr;
	//std::cout << "Hello World!\n" << "Fib: " << val;
	add();
	addMixed();
	sub();
	addpar(1, 3);
	Literal res = addparret(1, 3);
	normaladd();
	declare1();
	declare2();
	Literal s = sum(Literal(std::list<Literal>({1, 2, 3})));
	std::cout << s.getInt() << std::endl;
	//str();
}
/*
Literal start()
{
	Literal Num;
	if (Num.type == Undefined)
		Num = Literal(43);
	Literal Pi;
	if (Pi.type == Undefined)
		Pi = Literal(3.14159);
	Literal Hello;
	if (Hello.type == Undefined)
		Hello = Literal((size_t)0);
	Literal OtherNode;
	if (OtherNode.type == Undefined)
		OtherNode = Literal((size_t)1);
	Literal StrangeString;
	if (StrangeString.type == Undefined)
		StrangeString = Literal("example@n\"ode");
	Literal Listing;
	if (Listing.type == Undefined)
		Listing = Literal({Literal(1), Literal(2), Literal(3), Literal(4)});
}
Literal start(Literal arg)
{
	if (arg == Literal(2))
	{ // fun (line 8) ///
		Literal Num;
		if (Num.type == Undefined)
			Num = Literal(43);
		Literal Pi;
		if (Pi.type == Undefined)
			Pi = Literal(3.14159);
		Literal Hello;
		if (Hello.type == Undefined)
			Hello = Literal((size_t)0);
		Literal OtherNode;
		if (OtherNode.type == Undefined)
			OtherNode = Literal((size_t)1);
		Literal StrangeString;
		if (StrangeString.type == Undefined)
			StrangeString = Literal("example@n\"ode");
		Literal Listing;
		if (Listing.type == Undefined)
			Listing = Literal({Literal(1), Literal(2), Literal(3), Literal(4)});
	}
}
*/