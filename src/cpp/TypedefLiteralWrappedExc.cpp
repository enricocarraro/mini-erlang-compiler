#include <iostream>
#include <string>
#include <stdio.h>
#include <list>
#include <vector>
#include <unordered_map>
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

void error(string error_message)
{
    throw invalid_argument(error_message);
}

void bad_matching_error()
{
    error("bad matching.");
}
typedef enum
{
    Integer,
    Float,
    List,
    Atom,
    Undefined,
    Boolean
} LiteralType;

typedef struct Literal
{
    LiteralType type = Undefined;
    void *ptr = nullptr;
    Literal(int value) : type(Integer), ptr(new int(value))
    {
    }
    Literal(double value) : type(Float), ptr(new double(value)) {}
    Literal(size_t value) : type(Atom), ptr(new size_t(value)) {}
    Literal(bool value) : type(Boolean), ptr(new bool(value)) {}
    Literal(list<Literal> value) : type(List)
    {
        debug("list constructor");
        if (value.size() == 0)
        {
            ptr = nullptr;
        }
        else
        {
            debug(value.begin()->getString('w'));
            if (value.begin()->type == Undefined)
            {
                error("undefined expression in list initialization: not allowed.");
            }

            ptr = new pair<Literal, Literal>(*value.begin(), list<Literal>(next(value.begin()), value.end()));
            debug("new list item");
        }
    }
    Literal(Literal head, Literal tail) : type(List)
    {
        debug("list constructor");
        if (head.type == Undefined || tail.type == Undefined)
        {
            error("undefined expression in list initialization is not allowed.");
        }
        ptr = new pair<Literal, Literal>(head, tail);
    }
    Literal() : type(Undefined) {}
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
            this->ptr = a.ptr != nullptr ? new pair<Literal, Literal>(*(pair<Literal, Literal> *)a.ptr) : nullptr;
            break;
        case Atom:
            this->ptr = new size_t(*(size_t *)a.ptr);
            break;
        case Boolean:
            this->ptr = new bool(*(bool *)a.ptr);
            break;
        case Undefined:
            error("bad copy.");
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
                this->ptr = match_var.ptr != nullptr ? new pair<Literal, Literal>(*(pair<Literal, Literal> *)match_var.ptr) : nullptr;

                break;
            case Atom:
                this->ptr = new size_t(*(size_t *)match_var.ptr);
                break;
            case Boolean:
                this->ptr = new bool(*(bool *)match_var.ptr);
                break;
            case Undefined:
                error("bad matching");
            }
        }

        if (*this != match_var)
        {
            error("bad matching");
        }
    }

    bool try_match(const Literal &match_var)
    {
        try
        {
            match(match_var);
        }
        catch (invalid_argument e)
        {
            return false;
        }
        return true;
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
            error("bad matching: not a list.");
        list<Literal> result;
        if (ptr != nullptr)
        {
            pair<Literal, Literal> element = *(pair<Literal, Literal> *)ptr;
            result.push_back(element.first);
            void *iterator = element.second.ptr;
            while (iterator != nullptr && element.second.type == List)
            {
                element = *(pair<Literal, Literal> *)iterator;
                result.push_back(element.first);
                iterator = element.second.ptr;
            }
        }
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
            if (ptr == nullptr && rhs.ptr == nullptr)
            {
                return true;
            }
            else
            {
                pair<Literal, Literal> lhs_pair = *(pair<Literal, Literal> *)ptr;
                pair<Literal, Literal> rhs_pair = *(pair<Literal, Literal> *)rhs.ptr;
                return lhs_pair.first == rhs_pair.first && lhs_pair.second == rhs_pair.second;
            }
            break;
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
        if (type != List)
        {
            error("bad matching: not a list.");
        }
        else if (!ptr)
        {
            error("bad matching.");
        }
        pair<Literal, Literal> element = *(pair<Literal, Literal> *)ptr;
        return element.first;
    }

    Literal listTail() const
    {
        debug("listTail");
        if (type != List)
        {
            error("bad matching: not a list.");
        }
        else if (!ptr)
        {
            error("bad matching.");
        }
        pair<Literal, Literal> element = *(pair<Literal, Literal> *)ptr;
        return element.second;
    }

    void deleteLiteral()
    {
        type = Undefined;
        if (ptr)
        {
            switch (type)
            {
            case List:
                delete (pair<Literal, Literal> *)ptr;
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

    // Returns the string representation of the Literal.
    // modes: 'w' and 's' correspond to the characters sequences usable in io:format()
    //
    string getString(char mode) const
    {
        if (type == Undefined)
        {
            error("Printing not supported for type " + literalType() + ".");
        }

        string ans = "";

        if (isNumber())
        {
            ans = type == Integer ? to_string(getInt()) : to_string(getFloat());
        }
        else if (type == Atom)
        {
            char str[40];
            sprintf(str, "$atom:%lu$", getAtom());
            ans = to_string(getAtom());
        }
        else if (type == List)
        {

            if (ptr == nullptr)
            {
                return ans;
            }
            pair<Literal, Literal> element = *(pair<Literal, Literal> *)ptr;
            if (mode == 's')
            {
                if (element.second.type != List)
                {
                    error("bad argument: improper lists cannot be printed as strings.");
                }
                if (element.first.type != Integer || element.first.getInt() < 0 || element.first.getInt() > 255)
                {

                    error("bad argument: cannot print type " + literalType() + " as a string.");
                }
                ans += (char)element.first.getInt() + element.second.getString(mode);
            }
            else if (element.second.type != List)
            {
                ans += element.first.getString(mode) + "|" + element.second.getString(mode);
            }
            else
            {
                ans += element.first.getString(mode) + (element.second.ptr != nullptr ? "," + element.second.getString(mode) : "");
                if (element.first.type == List)
                {
                    ans = "[" + ans + "]";
                }
            }
        }
        else if (type == Boolean)
        {
            ans += getBoolean() ? "true" : "false";
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
        case List:
            return "List";
        case Undefined:
            return "Undefined";
        case Boolean:
            return "Boolean";
        }
        return "";
    }

    bool isProperList() const
    {
        if (type != List)
            return false;
        if (type == List && ptr == nullptr)
            return true;
        return listTail().isProperList();
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

Literal listsnth(const Literal &a, const Literal &b)
{
    if (a.type != Integer || b.type != List)
    {
        error("bad argument\n\tin function lists:nth: first parameter must be an integer (" + a.literalType() + ") and second parameter must be a list (" + b.literalType() + ").");
    }
    int n = a.getInt();
    if (n <= 0)
    {
        error("bad argument\n\tin function lists:nth: first parameter must be greater than zero.");
    }

    pair<Literal, Literal> next = *(pair<Literal, Literal> *)b.ptr;
    for (size_t i = 0; i < n - 1; i++)
    {
        if (next.second.type != List || next.second.ptr == nullptr)
        {
            error("bad argument\n\tin function lists:nth: first parameter must not exceed the size of the list.");
        }
        next = *(pair<Literal, Literal> *)next.second.ptr;
    }

    return next.first;
}

Literal listsappend(const Literal &a, const Literal &b)
{
    if (a.type != List || b.type != List)
    {
        error("bad argument\n\tin function lists:append: needs 2 list parameters (a and b).");
    }
    if (!a.isProperList() || !b.isProperList())
    {
        error("bad argument\n\tin function lists:append: improper lists are not supported.");
    }

    list<Literal> concatenation = a.getList();
    list<Literal> b_list = b.getList();
    concatenation.insert(concatenation.end(), b_list.begin(), b_list.end());

    return Literal(concatenation);
}

// printf equivalent, supports in a limited way ~s and ~w control sequences.
// Example (Erlang): io:format("Format String: ~w ~s ~n", [1, "hello"]) outputs "Format String 1 hello \n"
Literal ioformat(const Literal &format, const Literal &data)
{
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
        if (i++ >= llist.size())
        {
            error("bad argument\n\tin function io:format: data control sequences are more than elements in data list.");
        }
        to_print += mm.prefix().str() + mm.format("$2");
        if (mm.format("$3").compare("w") == 0)
        {
            if (llist_it->type == List)
            {
                to_print += "[" + llist_it->getString('w') + "]";
            }
            else
            {
                to_print += llist_it->getString('w');
            }
        }
        else
        {
            to_print += llist_it->getString('s');
        }

        llist_it = next(llist_it);
        ss = mm.suffix().str();
    }

    if (i < llist.size())
    {
        error("bad argument\n\tin function io:format: data control sequences are less than elements in data list.");
    }

    cout << to_print << ss;

    return Literal(true);
}

Literal ioformat(const Literal &format)
{
    if (format.type != List)
    {
        error("bad argument\n\tin function io:format: needs a list parameter (format).");
    }

    regex n("(([^~]|^)(~n))");

    cout << regex_replace(format.getString('s'), n, "$2\n");

    return Literal(true);
}

bool eval_guard(Literal guard_expressions)
{
    if (guard_expressions.type != List)
    {
        error("invalid guard expression.");
    }
    list<Literal> guard_expressions_list = guard_expressions.getList();
    for (auto &guard_expression : guard_expressions_list)
    {
        if (guard_expression.type != Boolean)
        {
            error("invalid guard expression: must return boolean value.");
        }
        if (!guard_expression.getBoolean())
        {
            return false;
        }
    }
    return true;
}

void testbifs()
{
    Literal atom((size_t)4);
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
    // assert(BIF_length(smallList).getInt() == 10);
    //assert(BIF_hd(smallList).getInt() == 0);
    //assert(BIF_hd(BIF_tl(smallList)).getInt() == 1);
    //    assert(BIF_length(BIF_tl(smallList)).getInt() == 9);

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
    Literal someList(list<Literal>({99, 97, 101, 120}));
    Literal listhead(someList.listHead());
    Literal listTail = someList.listTail();
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

Literal testTryMatchVar(Literal n)
{
    Literal ret;
    if (n.try_match(4))
    {
        ret = Literal(50);
    }
    else if (n.try_match(50))
    {
        ret = Literal(4);
    }
    else
    {
        error("error");
    }

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
}
void declare_atom()
{
    Literal atom((size_t)0);
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

    bad_matching_error();
    return false;
}

Literal store2ItemIntList()
{
    Literal f = 104;
    Literal s = 101;
    Literal a;
    a.match(list<Literal>({f, s}));
    Literal b(a);
    return b;
}
void printIntList()
{
    Literal f = 104;
    Literal s = 101;
    Literal t = 121;
    Literal fo = 98;
    Literal fi = 111;
    Literal oto = 121;
    Literal a(list<Literal>({f, s, t, fo, fi, oto}));
    a.getList();
    Literal al = Literal(Literal(4), a);
    Literal b = stringToList("heyboy");
    ioformat(stringToList("a: ~s, ~w, b: ~s, ~w ~n"), list<Literal>({a, a, b, b}));
    ioformat(stringToList("~w ~n"), list<Literal>({stringToList("ciao~n")}));
    ioformat(stringToList("~s ~n"), list<Literal>({stringToList("ciao~n")}));
    //ioformat(stringToList("ciao~n"));
}

void emptyList()
{
    Literal a(list<Literal>({}));
}

void listComprehension(Literal xll, Literal yll)
{
    //Literal xll(list<Literal>({Literal(1), Literal(2), Literal(3)}));
    //Literal yll(list<Literal>({Literal(4), Literal(5), Literal(6)}));
    list<Literal> xlist = xll.getList();
    list<Literal> ylist = yll.getList();
    list<Literal> res({});
    for (auto x : xlist)
    {
        if (x.lesseq(2).getBoolean())
        {
            res.insert(res.end(), Literal(3) + x);
        }
        /*     for (auto &y : ylist)
        {
            if (x.lesseq(2).getBoolean() && y.greatereq(5).getBoolean())
            {
                res.insert(res.end(), y + x);
            }
        }*/
    }
    ioformat(stringToList("res: ~w~n"), list<Literal>({res}));
}

void listComprehension2(Literal xll, Literal yll)
{
    //Literal xll(list<Literal>({Literal(1), Literal(2), Literal(3)}));
    //Literal yll(list<Literal>({Literal(4), Literal(5), Literal(6)}));
    //
    list<Literal> xlist = xll.getList();
    list<Literal> ylist = yll.getList();
    list<Literal> res({});
    for (auto x : xlist)
    {
        for (auto &y : ylist)
        {
            if (eval_guard(Literal(x.lesseq(2).getBoolean(), Literal(y.greatereq(5).getBoolean(), list<Literal>()))))
            {
                res.insert(res.end(), y + x);
            }
        }
    }
    ioformat(stringToList("res: ~w~n"), list<Literal>({res}));
}

int main()
{
    printf("minierlangVM started\n");
    try
    {

        // cout << "works: " << literalType(ret) <<  " " << ret.getString('s') << endl;
        //printIntList();
        placeholder();
#if DEBUG

        easystore();
        Literal ret = easystore();
        cout << "works: " << ret.literalType() << " " << ret.getString('s') << endl;
        Literal a(5);
        Literal tail(list<Literal>({}));
        Literal ll(a, tail);
        Literal oneto5(list<Literal>({Literal(1), Literal(2), Literal(3), Literal(4), Literal(5)}));
        ioformat(stringToList("onetofive: ~w ~n"), list<Literal>({oneto5}));
        ioformat(stringToList("onetofive -> 3:~w 4 ~w~n"), list<Literal>({listsnth(Literal(3), oneto5), listsnth(Literal(4), oneto5)}));
        ioformat(stringToList("onetofive -> 1:~w 5 ~w~n"), list<Literal>({listsnth(Literal(1), oneto5), listsnth(Literal(5), oneto5)}));
        ioformat(stringToList("onetofive append to ll:~w ll append to onetofive ~w~n"), list<Literal>({listsappend(ll, oneto5), listsappend(oneto5, ll)}));
        cout << listsnth(Literal(1), oneto5).literalType() << endl;
        ioformat(stringToList("ll -> ~w~n"), list<Literal>({Literal(1)}));
        Literal badret = badMatchtest();
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
