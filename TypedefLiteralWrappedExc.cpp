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
	throw std::invalid_argument(error_message);	
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
			return getInt() % a.getInt() == 0 ? Literal(getInt() / a.getInt()) : Literal(getInt() / (double)  a.getInt());
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

	Literal integerDiv(Literal const &a)
	{
		debug("integerDiv operator");
		if (!(type == Integer && a.type == Integer))
		{
			error("Invalid integer division between incompatible terms.");
        }   
		
		return Literal(getInt() / a.getInt());
	}

	Literal operator%(Literal const &a)
	{
		debug("operator%");
		if (!(type == Integer && a.type == Integer)){
			error("Invalid arithmetic expression, modulo accepts only integers.");
		}
		
		return Literal(getInt() % a.getInt());
	}
	
	Literal less(const Literal &rhs) const
	{
		return *this < rhs;
	}

	Literal lesseq(const Literal &rhs) const
	{
		return *this <= rhs;
	}

	Literal greater(const Literal &rhs) const
	{
		return *this > rhs;
	}

	Literal greatereq(const Literal &rhs) const
	{
		return *this >= rhs;
	}

	Literal exequal(const Literal &rhs) const
	{
		return *this == rhs;
	}

	Literal notexequal(const Literal &rhs) const
	{
		return *this != rhs;
	}

	Literal equal(const Literal &rhs) const
	{
		if (*this == rhs)
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

	Literal notequal(const Literal &rhs) const
	{
		return (*this < rhs) || (rhs < *this);
	}

	Literal operator&(Literal const &a)
	{
		debug("operator&");
		if (!(type == Boolean && a.type == Boolean))
		{
			error("Invalid AND operation between incompatible terms.");
        }   
		
		return getBoolean() && a.getBoolean();
	}

	Literal operator|(Literal const &a)
	{
		debug("operator|");
		if (!(type == Boolean && a.type == Boolean))
		{
			error("Invalid OR operation between incompatible terms.");
        }   
		
		return getBoolean() || a.getBoolean();
	}

	Literal operator^(Literal const &a)
	{
		debug("operator^");
		if (!(type == Boolean && a.type == Boolean))
		{
			error("Invalid XOR operation between incompatible terms.");
        }   
		
		return (bool)(getBoolean() ^ a.getBoolean());
	}

	Literal operator!()
	{
		debug("operator!");
		if (type != Boolean)
		{
			error("Invalid NOT operation with incompatible term.");
        }   
		
		return !getBoolean();
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


void add() {
	Literal one(1);
	Literal three(3);
	Literal four = three + one;
#if TEST
	assert(four.getInt() == (1 + 3));
#endif
	//std::cout << "Four: " << four.getInt() << std::endl;
}

void div() {
	Literal a(1), b(2);
	Literal r = a/b;
#if TEST
	assert(r.getFloat() == 1/2.0);
#endif

}

void integerdiv() {
	Literal a(5), b(2);
	Literal r = a.integerDiv(b);
#if TEST
	assert(r.getInt() == 5/2);
#endif
}

void rem() {
	Literal a(5), b(2);
	Literal r = a % b;
#if TEST
	assert(r.getInt() == (5 % 2));
#endif
}

void mul() {
	Literal a(5), b(2.5);
	Literal r = a * b;
#if TEST
	assert(r.getFloat() == (5 * 2.5));
#endif
}

void comparisons() {
	Literal a(1), b(2);
	Literal a_lesser = a.less(b);
	Literal a_lessereq = a.lesseq(b);
	Literal b_lesser = a.greater(b);
	Literal b_lessereq = a.greatereq(b);
#if TEST
	assert(a_lesser.getBoolean() == (1 < 2));
	assert(a_lessereq.getBoolean() == (1 <= 2));
	assert(b_lesser.getBoolean() == (1 > 2));
	assert(b_lessereq.getBoolean() == (1 >= 2));
#endif
	Literal c(1), d(1.0);
	Literal eq = c.equal(d);
	Literal exact_eq = c.exequal(d);
	Literal noteq = c.notequal(d);
	Literal neeq = c.notexequal(d);

	
#if TEST
	assert(eq.getBoolean() == true);
	assert(exact_eq.getBoolean() == false);
	assert(noteq.getBoolean() == false);
	assert(neeq.getBoolean() == true);
#endif
}

void booleanops() {
	Literal a(true), b(false);
	Literal logical_and = a & b;
	Literal logical_or = a | b;
	Literal logical_xor = a ^ b;
	Literal not_a = !a;
#if TEST
	assert(logical_and.getBoolean() == (true && false)) ;
	assert(logical_or.getBoolean() == (true || false)) ;
	assert(logical_xor.getBoolean() == (true ^ false)) ;
	assert(not_a.getBoolean() == !true) ;
#endif
}

void addpar(Literal one, Literal three) {
	Literal four = three + one;
#if TEST
	assert(four.getInt() == (1 + 3));
#endif
	//std::cout << "Four: " << four.getInt() << std::endl;
}
Literal standardfun(Literal param) {
	Literal somone = param + 2;
	Literal someop = somone;
	return someop;
}
Literal standardfunnopar() {
	Literal someop(2);
	return someop + 4;
}
Literal addparret(Literal one, Literal three) {
	Literal four = three + one;
	Literal ret = four + 5;
	ret = ret - 5;
#if TEST
	assert(four.getInt() == (1 + 3));
#endif
	return ret;
	//std::cout << "Four: " << four.getInt() << std::endl;
}

void addMixed() {
	Literal one(1);
	Literal pi(3.141592);
	Literal oneppi = pi + one;
#if TEST
	assert(oneppi.getFloat() == ((double)3.141592 + 1));
#endif

	Literal undef;

	try {
		Literal impossible = undef + one;
#if TEST
		assert(false);
#endif
	} catch (std::invalid_argument& e) {
	}

	//std::cout << "One Plus Pi: " << oneppi.getFloat() << std::endl;
}
void normaladd() {
	int a = 4, b = 5;
	int c = a + b;
	printf("%d\n", c);
}
void sub() {
	Literal one(1);
	Literal three(3);
	Literal two = three - one;
#if TEST
	assert(two.getInt() == (3 - 1));
#endif
	//std::cout << "Two: " << two.getInt() << std::endl;
}
void negat() {
	Literal one(1);
	Literal minusone = one.negative();
#if TEST
	assert(minusone == one.getInt()*-1);
#endif
	//std::cout << "Two: " << two.getInt() << std::endl;
}

void store_list() {
	Literal list({Literal(1), Literal(2), Literal (3)});
	//std::cout << "Two: " << two.getInt() << std::endl;
}
void declare_atom() {
	Literal atom ((size_t) 5);
	Literal atom1 ((size_t) 6);
	Literal atom2 ((size_t) 7);
	Literal atom3 ((size_t) 9);
	Literal atom4 ((size_t) 12);
	Literal atom5 ((size_t) 12);
}
void declare_float() {
	Literal f(3.0);
}
void declare_bool() {
	Literal boolea (true);

	//std::cout << "Two: " << two.getInt() << std::endl;
}
void vector_store_sum() {
	int v[5] = {1, 2, 3, 4, 5};
	size_t val = 50;
	int res = v[1] + v[2];
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
	try
	{

		add();
		addMixed();
		sub();
		addpar(1, 3);
		Literal res = addparret(1, 3);
		normaladd();
		vector_store_sum();
		store_list();
		declare_atom();
		declare_float();
		declare_bool();
		Literal standardret = standardfun(1);
		Literal s = sum(Literal(std::list<Literal>({1, 2, 3})));
		std::cout << s.getInt() << std::endl;
		standardfunnopar();
		comparisons();
		booleanops();
		rem();
		mul();
		div();
		integerdiv();
		negat();
		//str();
	}
	catch (const std::invalid_argument &e)
	{
		std::cerr << "Exception: " << e.what() << std::endl;
	}
}