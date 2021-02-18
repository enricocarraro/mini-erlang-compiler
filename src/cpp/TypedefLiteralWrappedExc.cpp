#include <iostream>
#include <string>
#include <stdio.h>
#include <list>
#include <vector>
#include <stdexcept>
#include <cassert>
#include <regex>

#if !DEBUG
#define NDEBUG
#endif

using namespace std;

void debug(string str)
{
#if DEBUG
	printf("%s\n", str.c_str());
#endif
}

void error(string error_message, int error_code)
{
	throw invalid_argument(error_message);
}

void error(string error_message)
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
	LiteralType type = Undefined;
	void *ptr = nullptr;
	Literal(int value) : type(Integer), ptr(new int(value))
	{
		ptr = new int(value);
		int ptr_val = *(int *)ptr;
	}
	Literal(double value) : type(Float), ptr(new double(value)) {}
	Literal(size_t value) : type(Atom), ptr(new size_t(value))
	{
	}
	Literal(bool value) : type(Boolean), ptr(new bool(value)) {}
	//Literal(vector<Literal> value) : type(List), ptr(new list<Literal>(value.begin(), value.end())) {}
	Literal(list<Literal> value) : type(List), ptr(new list<Literal>(value))
	{
		debug("list constructor");
	}
	//Literal(string value) : type(String), ptr(new string(value)) {}
	//Literal(FunctionMeta value) : type(Function), ptr(new FunctionMeta(value)) {}
	Literal() : type(Undefined) {}
	/*        Literal(Literal&& a)
    {
#if DEBUG
        cout << "move called" << endl;
#endif
        
        this->type = a.type;
        this->ptr = a.ptr;
        a.type = Undefined;
        a.ptr = nullptr;
    } */
	Literal(const Literal &a)
	{
		debug("copy called");

		this->type = a.type;
		switch (this->type)
		{
		case Integer:
			this->ptr = new int(*(int *)a.ptr);
			break;
		case Float:
			this->ptr = new double(*(double *)a.ptr);
			break;
		case List:
			this->ptr = new list<Literal>(*(list<Literal> *)a.ptr);
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
			error("Bad Copy");
		}
	}

	void match(const Literal &match_var)
	{
		debug("match called");

		if (this->type == Undefined)
		{
			this->type = match_var.type;
			switch (this->type)
			{
			case Integer:
				this->ptr = new int(*(int *)match_var.ptr);
				break;
			case Float:
				this->ptr = new double(*(double *)match_var.ptr);
				break;
			case List:
				this->ptr = new list<Literal>(*(list<Literal> *)match_var.ptr);
				break;
			case Function:
				this->ptr = match_var.ptr;
				break;
			case Atom:
				this->ptr = new size_t(*(size_t *)match_var.ptr);
				break;
			case Boolean:
				this->ptr = new bool(*(bool *)match_var.ptr);
				break;
			case Undefined:
				error("Bad Matching");
			}
		}

		if (*this != match_var)
		{
			error("Bad Matching");
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

	list<Literal> getList() const
	{
		debug("getList");
		if (type != List)
			error("Type error: not a list.");
		list<Literal> result = *(list<Literal> *)this->ptr;
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

	Literal listHead() const
	{
		debug("listHead");
		if ((type != List) || !ptr)
		{
			if (!ptr)
				// cout << "something wrong";
				error("head error type");
		}
		list<Literal> *ptrl = (list<Literal> *)ptr;
		if (ptrl->empty())
		{
			error("head error");
		}
		return Literal(*ptrl->begin());
	}

	Literal listTail() const
	{
		debug("listTail");
		if ((type != List) || !ptr)
		{
			error("tail error type");
		}
		list<Literal> *ptrl = (list<Literal> *)ptr;
		if (ptrl->empty())
		{
			error("tail error");
		}
		list<Literal> result(next(ptrl->begin()), ptrl->end());
		return result;
	}

	void deleteLiteral()
	{
		type = Undefined;
		if (ptr)
		{
			switch (type)
			{
			case List:
				delete (list<Literal> *)ptr;
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
			return getInt() % a.getInt() == 0 ? Literal(getInt() / a.getInt()) : Literal(getInt() / (double)a.getInt());
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
		if (!(type == Integer && a.type == Integer))
		{
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

	// TODO: add version that prints list like [1,2,3,4]
	string getString(char mode) const
	{
		if (!isNumber() && type != Atom && type != List)
		{
			error("Printing not supported for type " + literalType() + ".");
		}

		string ans = "";

		if (isNumber())
		{
			char str[40];
			ans = type == Integer ? to_string(getInt()) : to_string(getFloat());
		}
		else if (type == Atom)
		{
			char str[40];
			sprintf(str, "%lu", getAtom());
			ans = string(str);
		}
		else if (type == List)
		{
			list<Literal> llist = getList();

			for (Literal c : llist)
			{
				if (mode == 'w')
				{
					ans += c.getString('w') + ",";
				}
				else if (mode == 's')
				{
					if (c.type != Integer || c.getInt() < 0 || c.getInt() > 255)
					{
						error("Cannot print term as a string.");
					}
					ans += (char)c.getInt();
				}
			}
			if (mode == 'w')
			{
				ans = "[" + ans;
				if (llist.size() > 0)
				{
					ans = ans.substr(0, ans.size() - 1);
				}

				ans += "]";
			}
		}
		return ans;
	}

	string literalType() const
	{
		switch (type)
		{
		case Integer:
			return "Integer";
		case Float:
			return "Float";
		case Atom:
			return "Atom";
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

} Literal;

// BIFs allowed in Guards
Literal BIF_is_atom(const Literal &l)
{
	return l.type == Atom;
}

Literal BIF_is_boolean(const Literal &l)
{
	return l.type == Boolean;
}

Literal BIF_is_float(const Literal &l)
{
	return l.type == Float;
}

Literal BIF_is_integer(const Literal &l)
{
	return l.type == Integer;
}

Literal BIF_is_function(const Literal &l)
{
	return l.type == Function;
}

Literal BIF_is_list(const Literal &l)
{
	return l.type == List;
}

Literal BIF_is_number(const Literal &l)
{
	return l.isNumber();
}

// Other BIFs
Literal BIF_abs(const Literal &l)
{
	if (!l.isNumber())
	{
		error("abs can only be applied to numbers.");
	}
	if (l.type == Integer)
	{
		int val = l.getInt();
		if (val >= 0)
		{
			return val;
		}
		return -val;
	}
	double val = l.getFloat();
	if (val >= 0)
	{
		return val;
	}
	return -val;
}

Literal BIF_float(const Literal &l)
{
	if (!l.isNumber())
	{
		error("float can only be applied to numbers.");
	}
	if (l.type == Integer)
	{
		return (double)l.getInt();
	}
	return l;
}

Literal BIF_hd(const Literal &l)
{
	return l.listHead();
}

Literal BIF_tl(const Literal &l)
{
	return l.listTail();
}

Literal BIF_length(const Literal &l)
{
	// For simplicity, we assume that list can have at most 2^31 - 1 elements.
	return (int)l.getList().size();
}

Literal BIF_round(const Literal &l)
{
	if (!l.isNumber())
	{
		error("float can only be applied to numbers.");
	}
	if (l.type == Integer)
	{
		return l.getInt();
	}
	return (int)(l.getFloat() + 0.5);
}

Literal BIF_trunc(const Literal &l)
{
	if (!l.isNumber())
	{
		error("float can only be applied to numbers.");
	}
	if (l.type == Integer)
	{
		return l.getInt();
	}
	return (int)l.getFloat();
}

Literal stringToList(string str)
{
	std::list<Literal> ans;

	for (auto c : str)
	{
		ans.push_back((int)c);
	}

	return ans;
}

// printf equivalent, supports in a limited way ~s and ~w control sequences.
// Example (Erlang): io:format("Format String: ~w ~s ~n", [1, "hello"]) outputs "Format String 1 hello \n"
Literal ioformat(const Literal &format, const Literal &data)
{
	// TODO: return Atom("ok") on success instead of boolean.
	if (format.type != List || data.type != List)
	{
		error("bad argument\n\tin function io:format: needs 2 list parameters (format and data).");
	}

	list<Literal> llist = data.getList();

	regex n("(([^~]|^)(~n))");
	regex ee("(([^~]|^)~(w|s))");

	string ss = format.getString('s');
	ss = regex_replace(ss, n, "$2\n");

	smatch mm;

	string to_print = "";

	auto llist_it = llist.begin();
	int i = 0;
	while (regex_search(ss, mm, ee))
	{
		if(i++ >= llist.size()) {
			error("bad argument\n\tin function io:format: data control sequences are more than elements in data list.");
		}
		to_print += mm.prefix().str() +  mm.format("$2");
		to_print += llist_it->getString(mm.format("$3").compare("w") == 0 ? 'w' : 's');
		
		llist_it = next(llist_it);
		ss = mm.suffix().str();
	}

	if(i < llist.size()) {
		error("bad argument\n\tin function io:format: data control sequences are less than elements in data list.");
	}

	cout << to_print << ss;

	return Literal(true);
}


Literal ioformat(const Literal &format) {
		// TODO: return Atom("ok") on success instead of boolean.
	if (format.type != List)
	{
		error("bad argument\n\tin function io:format: needs a list parameter (format).");
	}

	regex n("(([^~]|^)(~n))");	

	cout << regex_replace(format.getString('s'), n, "$2\n");

	return Literal(true);
}

void testbifs()
{
	Literal atom((size_t)0);
	Literal notAnAtom(4);
	Literal boolean(true);
	Literal notABoolean({1, 3, 4, 5});

	Literal negativeFloat(-5.3);
	Literal negativeInt(-5);
	Literal positiveFloat(5.2);
	Literal positiveInt(4);

	Literal smallList({0, 1, 1, 2, 3, 5, 8, 13, 21, 34});

	char strlist[5][10] = {"ai", "ei", "ui", "aei"};
	char *a = strlist[0];
	char *e = strlist[1];

#if TEST
	assert(BIF_is_atom(atom).getBoolean());
	assert(!BIF_is_atom(notAnAtom).getBoolean());
	assert(BIF_is_boolean(boolean).getBoolean());
	assert(!BIF_is_boolean(notABoolean).getBoolean());

	assert(BIF_abs(negativeFloat).getFloat() == 5.3);
	assert(BIF_abs(negativeInt) == 5);
	assert(BIF_abs(positiveFloat).getFloat() == 5.2);
	assert(BIF_abs(positiveInt) == 4);

	assert(BIF_abs(positiveFloat).getFloat() == 5.2);
	assert(BIF_abs(positiveInt).getInt() == 4);

	assert(BIF_float(2).getFloat() == 2.0);
	assert(BIF_length(smallList).getInt() == 10);
	assert(BIF_hd(smallList).getInt() == 0);
	assert(BIF_hd(BIF_tl(smallList)).getInt() == 1);
	assert(BIF_length(BIF_tl(smallList)).getInt() == 9);

	assert(BIF_trunc(5.3).getInt() == 5);
	assert(BIF_round(5.5).getInt() == 6);
#endif
}

void add()
{
	Literal one(1);
	Literal three(3);
	Literal four = three + one;
#if TEST
	assert(four.getInt() == (1 + 3));
#endif
	//cout << "Four: " << four.getInt() << endl;
}

void div()
{
	Literal a(1), b(2);
	Literal r = a / b;
#if TEST
	assert(r.getFloat() == 1 / 2.0);
#endif
}

void integerdiv()
{
	Literal a(5), b(2);
	Literal r = a.integerDiv(b);
#if TEST
	assert(r.getInt() == 5 / 2);
#endif
}

void rem()
{
	Literal a(5), b(2);
	Literal r = a % b;
#if TEST
	assert(r.getInt() == (5 % 2));
#endif
}

void mul()
{
	Literal a(5), b(2.5);
	Literal r = a * b;
#if TEST
	assert(r.getFloat() == (5 * 2.5));
#endif
}

void comparisons()
{
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

void booleanops()
{
	Literal a(true), b(false);
	Literal logical_and = a & b;
	Literal logical_or = a | b;
	Literal logical_xor = a ^ b;
	Literal not_a = !a;
#if TEST
	assert(logical_and.getBoolean() == (true && false));
	assert(logical_or.getBoolean() == (true || false));
	assert(logical_xor.getBoolean() == (true ^ false));
	assert(not_a.getBoolean() == !true);
#endif
}

Literal badMatchtest()
{
	Literal Num;
	Num.match(4);
	Literal ret(Num);
	return ret;
}

Literal easystore()
{
	Literal Num(2);
	Literal ret(Num);
	return ret;
}

Literal placeholder()
{
	return 1;
}

void addpar(Literal one, Literal three)
{
	Literal four = three + one;
#if TEST
	assert(four.getInt() == (1 + 3));
#endif
	//cout << "Four: " << four.getInt() << endl;
}
Literal standardfun(Literal param)
{
	Literal somone = param + 2;
	Literal someop = somone;
	return someop;
}
Literal standardfunnopar()
{
	Literal someop(2);
	return someop + 4;
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
	//cout << "Four: " << four.getInt() << endl;
}

void addMixed()
{
	Literal one(1);
	Literal pi(3.141592);
	Literal oneppi = pi + one;
#if TEST
	assert(oneppi.getFloat() == ((double)3.141592 + 1));
#endif

	Literal undef;

	try
	{
		Literal impossible = undef + one;
#if TEST
		assert(false);
#endif
	}
	catch (invalid_argument &e)
	{
	}

	//cout << "One Plus Pi: " << oneppi.getFloat() << endl;
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
	//cout << "Two: " << two.getInt() << endl;
}
void negat()
{
	Literal one(1);
	Literal minusone = one.negative();
#if TEST
	assert(minusone == one.getInt() * -1);
#endif
	//cout << "Two: " << two.getInt() << endl;
}

void store_list()
{
	Literal list({Literal(1), Literal(2), Literal(3)});
	//cout << "Two: " << two.getInt() << endl;
}
void declare_atom()
{
	Literal atom((size_t)5);
	Literal atom1((size_t)6);
	Literal atom2((size_t)7);
	Literal atom3((size_t)9);
	Literal atom4((size_t)12);
	Literal atom5((size_t)12);
}
void declare_float()
{
	Literal f(3.0);
}
void declare_bool()
{
	Literal boolea(true);

	//cout << "Two: " << two.getInt() << endl;
}
void vector_store_sum()
{
	int v[5] = {1, 2, 3, 4, 5};
	size_t val = 50;
	int res = v[1] + v[2];
	//cout << "Two: " << two.getInt() << endl;
}
/*void str() {
    Literal one("one");
    Literal oneprime({'o', 'n', 'e'});
    //assert(one.getList() == oneprime.getList());
    debug("str");
    for(auto c: oneprime.getList()) {
        cout << c.getString();
    }
    cout << endl;
} */

Literal sum(Literal L, Literal N);
Literal sum(Literal L);

Literal sum(Literal L)
{
	return sum(L, 0);
}
Literal sum(Literal L, Literal N)
{

	if (L == Literal(list<Literal>()))
		return N;
	if (L.type == List)
		return sum(L.listTail(), L.listHead() + N);

	error("bad matching");
	return false;
}

Literal store2ItemIntList() { 
	Literal f = 104;
	Literal s = 101;
	Literal a;
	a.match(list<Literal>({f, s}));
	Literal b(a);
	return b;
}
void printIntList() {
	Literal f = 104;
	Literal s = 101;
	Literal t = 121;
	Literal fo = 98;
	Literal fi = 111;
	Literal oto = 121;
	Literal a(list<Literal>({f,s,t,fo,fi,oto}));
	a.getList();
	Literal al = Literal(a);
	Literal b = stringToList("heyboy");
	ioformat(stringToList("~s: ~w ~n"), list<Literal>({a, b}));
	ioformat(stringToList("~w ~n"), list<Literal>({stringToList("ciao~n")}));
	ioformat(stringToList("~s ~n"), list<Literal>({stringToList("ciao~n")}));
	ioformat(stringToList("ciao~n"));
}

void emptyList() {
	Literal a(list<Literal>({}));
}

int main()
{
	printf("minierlangVM started\n");
	try
	{

		//    Literal ret = badMatchtest();
		// cout << "works: " << literalType(ret) <<  " " << ret.getString('s') << endl;

        placeholder();
#if DEBUG
		
        easystore();
        Literal ret = easystore();
        cout << "works: " << ret.literalType() << " " << ret.getString('s') << endl;
#endif

#if TEST
		testbifs();
		standardfunnopar();
		add();
		comparisons();
		booleanops();
		rem();
		mul();
		div();
		integerdiv();
		negat();
#endif
		// addMixed();
		// sub();
		// addpar(1, 3);
		// Literal res = addparret(1, 3);
		// normaladd();
		// vector_store_sum();
		// store_list();
		// declare_atom();
		// declare_float();
		// declare_bool();
		// Literal standardret = standardfun(1);
		// Literal s = sum(Literal(list<Literal>({1, 2, 3})));
		// cout << s.getInt() << endl;

		//str();
	}
	catch (const invalid_argument &e)
	{
		cerr << "Error: " << e.what() << endl;
	}
}
