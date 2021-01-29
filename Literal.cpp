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
typedef struct
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
		return Literal(getFloat() / (double) a.getInt());
	}

    Literal operator^(Literal const &a)
    {

		debug("operator^ (integer division)");
		if (!(type == Integer && a.type == Integer))
			error("Invalid integer division between incompatible terms.");
        }   
		
		return Literal(getInt() / a.getInt());
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

