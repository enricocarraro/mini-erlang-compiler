package minierlang;

public class Const {
    public static final String LITERAL_STRUCT = "struct.Literal";
    
    public static final String LITERAL_CONSTRUCT_INT = "@_ZN7LiteralC1Ei";
    public static final String LITERAL_CONSTRUCT_FLOAT ="@_ZN7LiteralC1Ed";
    public static final String LITERAL_CONSTRUCT_ATOM = "@_ZN7LiteralC1Em";
    public static final String LITERAL_CONSTRUCT_BOOLEAN = "@_ZN7LiteralC1Eb";
    public static final String LITERAL_CONSTRUCT_EMPTY = "@_ZN7LiteralC1Ev";
    public static final String LITERAL_CONSTRUCT_COPY = "@_ZN7LiteralC1ERKS_";

    public static final String LITERAL_DESTRUCT = "@_ZN7LiteralD1Ev";
    

    public static final String LITERAL_NEGATIVE = "@_ZNK7Literal8negativeEv";
    public static final String LITERAL_ADD = "@_ZN7LiteralplERKS_";
    public static final String LITERAL_SUB = "@_ZN7LiteralmiERKS_";
    public static final String LITERAL_MUL = "@_ZN7LiteralmlERKS_";
    public static final String LITERAL_DIV = "@_ZN7LiteraldvERKS_";
    public static final String LITERAL_INT_DIV = "@_ZN7Literal10integerDivERKS_";
    public static final String LITERAL_MOD = "@_ZN7LiteralrmERKS_";
    
    public static final String LITERAL_NOT = "@_ZN7LiteralntEv";
    public static final String LITERAL_AND = "@_ZN7LiteralanERKS_";
    public static final String LITERAL_OR = "@_ZN7LiteralorERKS_";
    public static final String LITERAL_XOR = "@_ZN7LiteraleoERKS_";


    public static final String LITERAL_MATCH = "@_ZN7Literal5matchERKS_";

    public static final String LITERAL_EQ = "@_ZNK7Literal5equalERKS_";
    public static final String LITERAL_NOT_EQ = "@_ZNK7Literal8notequalERKS_";
    public static final String LITERAL_EXACT_EQ = "@_ZNK7Literal7exequalERKS_";
    public static final String LITERAL_EXACT_NOT_EQ = "@_ZNK7Literal10notexequalERKS_";
    public static final String LITERAL_LESS = "@_ZNK7Literal4lessERKS_";
    public static final String LITERAL_LESS_EQ = "@_ZNK7Literal6lesseqERKS_";
    public static final String LITERAL_GREATER = "@_ZNK7Literal7greaterERKS_";
    public static final String LITERAL_GREATER_EQ = "@_ZNK7Literal9greatereqERKS_";

    public static final String BASIC_STRING = "class.std::__1::basic_string";
    public static final String BASIC_STRING_CONSTRUCT = "@_ZNSt3__112basic_stringIcNS_11char_traitsIcEENS_9allocatorIcEEEC1IDnEEPKc";
    public static final String BASIC_STRING_DESTRUCT = "@_ZNSt3__112basic_stringIcNS_11char_traitsIcEENS_9allocatorIcEEED1Ev";
    
    public static final String BAD_MATCHING_CONST = "@.str.10";
    public static final String THROW_ERROR = "@_Z5errorNSt3__112basic_stringIcNS_11char_traitsIcEENS_9allocatorIcEEEE";
}