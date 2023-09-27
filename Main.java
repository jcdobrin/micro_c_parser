/*
 * Jason Dobrinski
 * Programming Language Concepts Final Project
 * Micro C language Lexical Scanner and Parser
 * Spring 2008
 */

package parser;
import java.io.*;
import java.util.*;
import java.lang.*;

class TokenItem
{
        public String classif;
        public String data;
        
        public TokenItem(String classif, String data)
        {
            this.classif = classif;
            this.data = data;
            
        }
}
class BinaryTreeNode
{

    String tok;
    BinaryTreeNode RightChild;
    BinaryTreeNode LeftChild;
    public BinaryTreeNode(String tok)
    {
       this.tok = tok;
       RightChild=null;
       LeftChild=null;
    }
   
    public void addRightChild(BinaryTreeNode RightChild)
    {
        this.RightChild = RightChild;
       
    }
    public void addLeftChild(BinaryTreeNode LeftChild)
    {
        this.LeftChild = LeftChild;
    }
}
class BinaryTree
{
    BinaryTreeNode Head;
    
    public BinaryTree(BinaryTreeNode Head)
    {
        this.Head = Head;
    }
    public void print()
    {
        
        System.out.println(Head.tok);
        
        if(Head.LeftChild != null)
        {
            
            recPrintLeft(Head.LeftChild,1);
        }
         
        
    }
    public void recPrintLeft(BinaryTreeNode Head,int i)
    {
        if(Head.tok != "")
        {
        for(int j = 0; j!=i; j++)
        {
        System.out.print(".");
        }
        System.out.println(Head.tok);
        }
         
         if(Head.LeftChild != null )
          recPrintLeft(Head.LeftChild,i+1);
       if(Head.RightChild != null )
        recPrintLeft(Head.RightChild,i);   
        
        
        }
        
}
class Parser
{
    public TokenItem tokenItem = new TokenItem("","");
    public TokenItem prevtokenItem = new TokenItem("","");
    public String line;
    //public StringTokenizer st;
    public char[] chars = new char[512];
    public int count =0;
    public int prevscancount=0;
    public int counter = 0;
    public int i = 0;
    
    public BinaryTree Main ;
    public Stack mainStack = new Stack();
    public enum funcList
    {
        CONTINUE,TYPEDEF,DEFAULT,RETURN,SWITCH,PRINTF,BREAK,WHILE,SCANF,CONST,CASE,ELSE,FOR,IF,DO,EOF
    }
    
        public Parser(String filename)
        {
        File file = new File(filename);
        BufferedReader bf = null;
       try{
        bf = new BufferedReader(new FileReader(file));
       }
       catch(FileNotFoundException e){
       System.out.println("FILE NOT FOUND");
       }
       
        
        do
        {
            try{
                line = bf.readLine();
            }
            catch(IOException e)
            {}
            catch( NullPointerException e2)
            {}
            //st = new StringTokenizer(line);
            counter = 0;
            if(line != null)
            while(counter<line.length())
            {
                chars[i] = line.charAt(counter);
                i++;
                counter++;
            }
            chars[i]='/';
            i++;
            chars[i]='n';
            i++;
        }
        while(line!=null);
       
        chars[i] = ' ';
        i++;
        chars[i] = 'E';
        chars[i+1] = 'O';
        chars[i+2] = 'F';
        //begin(); //test code
        scanner();
        tiny();
        if(!tokenItem.classif.equals("EOF"))
        {
            System.out.println("ERROR END OF FILE NOT REACHED");
        }
    }
        public void begin()
        {
            scanner();
            System.out.println(tokenItem.classif + "    " + tokenItem.data);
            if(tokenItem.classif.equals("EOF"))
            {
                
            }
            else
            {
                begin();
            }
            
        }
        public void read(String s)
        {
            if(!tokenItem.classif.equals(s))
            {
                System.out.println("READ ERROR " + s + " EXPECTED BUT " +  tokenItem.classif + " WAS READ");
            }
            else
            {
                //System.out.println("READ "+ s);
                scanner();
            }
            
        }
        public void buildTree(String s, int i)
        {
            System.out.println(s +"    " + i);
        }
         public void buildTree(String s, int i,boolean scan)
        {
            
            BinaryTreeNode build = new BinaryTreeNode(s + "(" + i + ")");
            BinaryTreeNode temp = new BinaryTreeNode("");
            BinaryTreeNode h = new BinaryTreeNode("");
            if(i==0)
            {
                mainStack.push(build);
            }
            else
            {
                if(i>0)
                {
                    for(int n = 0; n<i; n++)
                    {
                        if(!mainStack.isEmpty())
                    h  = (BinaryTreeNode) mainStack.pop();
                    h.addRightChild(temp);
                    temp = h;
                    }
            
                  build.addLeftChild(temp);
                }
                mainStack.push(build);
             }  
         }
         public void tiny()
        {
             int n=0;
             while(tokenItem.classif.equals("typedef") || checkFunc())
             {
                 Outer();
                 n++;
             }
             do
            {
             Funcdef();
             n++;
             
            }
             while(tokenItem.classif.equals("identifier"));
            buildTree("program", n,false);
        }
        public void Outer()
        {
                if(tokenItem.classif.equals("typedef"))
                {
                   Typedef();
                }
                else
                 {          
                 
                 Var();
                 read(";");
                }
             
           }
        public boolean checkFunc()
        {
            TokenItem holdToken = new TokenItem(tokenItem.classif,tokenItem.data);
            int holdpos = count;
            scanner();
            scanner();
            
            if(tokenItem.classif.equals("("))
            {
                tokenItem.data = holdToken.data;
                tokenItem.classif = holdToken.classif;
                count = holdpos;
                return false;
            }
            else
            {
                tokenItem.data = holdToken.data;
                tokenItem.classif = holdToken.classif;
                count = holdpos;
                return true;
            }
        }
        public void Funcdef()
        {
            int n = 2;
            Name();
            Name();
            read("(");
            if(tokenItem.classif.equals(")"))
            {
                
                read(")");
                Codeblock();
                n++;
                buildTree("func", n,false);
            }
            else
            { 
                TokenItem holdToken = new TokenItem(tokenItem.classif,tokenItem.data);
                int holdpos = count;
                scanner();
                if(tokenItem.classif.equals(","))
                {
                    tokenItem.data = holdToken.data;
                    tokenItem.classif = holdToken.classif;
                    count = holdpos;
                    Namelist();
                    read(")");
                    do
                    {
                        Var();
                        read(";");
                        n++;
                    }
                    while(tokenItem.classif.equals("identifier"));
                    Codeblock();
                    buildTree("funcold",n,false);
                }
                else
                {
                    tokenItem.data = holdToken.data;
                    tokenItem.classif = holdToken.classif;
                    count = holdpos;
                    do
                    {
                        DefParam();
                        n++;
                        if(!tokenItem.classif.equals(')'))
                        read(",");
                        
                    }
                    while(!tokenItem.classif.equals(')'));
                    read(")");
                    Codeblock();
                    buildTree("func", n,false);
                }
             }
         }
        public void DefParam()
        {
            Name();
            Name();
            buildTree("var",2,false);
        }
        public void Typedef()
        {
            if(!tokenItem.classif.equals("typedef"))
            {
                System.out.println("ERROR TYPEDEF EXPECTED");
            }
            read("typedef");
            Name();
            Name();
            buildTree("typedef",2,false);
            read(";");
        }
        public void Namelist()
        {
            do
            {
                Name();
                if(tokenItem.classif.equals(","))
                read(",");
            }
            while(tokenItem.classif.equals("identifier"));
        }
        public void Statement()
        {
            if(tokenItem.classif.equals("if"))
            {
             read("if");
             read("(");
             TokenItem holdToken = new TokenItem(tokenItem.classif,tokenItem.data);
             int holdpos = count;
             scanner();
             if(!tokenItem.classif.equals("identifier"))
             {
                 count = holdpos;
                 tokenItem.data = holdToken.data;
                 tokenItem.classif = holdToken.classif;
                 
                 Expression();
                 read(")");
                 Statement();
                 int n = 2;
                 while(tokenItem.classif.equals("else"))
                 {
                     read("else");
                     if(tokenItem.classif.equals("if"))
                     read("if");
                     Statement();
                     n++;
                 }
                 buildTree("if", n, false);
             }
             else
             {
                 count = holdpos;
                 tokenItem.data = holdToken.data;
                 tokenItem.classif = holdToken.classif;
                 
                 Assign();
                 read(")");
                 Statement();
                 int n = 2;
                 while(tokenItem.classif.equals("else"))
                 {
                     read("else");
                     if(tokenItem.classif.equals("if"))
                     read("if");
                     Statement();
                     n++;
                 }
                 buildTree("if", n, false);
            }
          }
          else if(tokenItem.classif.equals("{"))
          {
                Codeblock();
          }
          else if(tokenItem.classif.equals("printf"))
          {
                read("printf");
                read("(");
                String();
                int n =1;
                if(tokenItem.classif.equals(","))
                    read(",");
                while(checkExpression())
                {
                    Expression();
                    n++;
                    if(tokenItem.classif.equals(","))
                    read(",");
                }
                read(")");
                read(";");
                buildTree("printf",n,false);
           }
          else if(tokenItem.classif.equals("scanf"))
          {
              read("scanf");
              read("(");
              String();
              int n = 1;
              if(tokenItem.classif.equals(","))
                    read(",");
              if(tokenItem.classif.equals("&"))
                    read("&");
                while(tokenItem.classif.equals("identifier"))
                {
                    Name();
                    n++;
                    if(tokenItem.classif.equals(","))
                    {
                        read(",");
                        if(tokenItem.classif.equals("&"))
                        {
                           read("&");
                        }
                        else
                        {
                            System.out.println("ERROR & EXPECTED");
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
                read(")");
                read(";");
                buildTree("scanf",n,false);
              }
            else if(tokenItem.classif.equals("while"))
            {
                read("while");
                read("(");
                Expression();
                read(")");
                Statement();
                buildTree("while", 2,false);
            }
            else if(tokenItem.classif.equals("do"))
            {
                read("do");
                Statement();
                read("while");
                read("(");
                Expression();
                read(")");
                read(";");
                buildTree("do",2,false);
                
            }
            else if(tokenItem.classif.equals("for"))
            {
                read("for");
                read("(");
                Statement();
                Statement();
                if(tokenItem.classif.equals(")"))
                {
                    Null();
                    read(")");
                    Statement();
                    buildTree("for", 4,false);
                }
                else if(tokenItem.classif.equals("identifier"))              
                {
                    int holdpos = count;
                    TokenItem holdToken = new TokenItem(tokenItem.classif,tokenItem.data);
                    scanner();
                    if(tokenItem.classif.equals("DEC")||tokenItem.classif.equals("INC"))
                    {
                        count = holdpos;
                        tokenItem.data = holdToken.data;
                        tokenItem.classif = holdToken.classif;
                        Expression();
                        read(")");
                        Statement();
                        buildTree("for",4,false);
                    }
                    else
                    {
                         count = holdpos;
                        tokenItem.data = holdToken.data;
                        tokenItem.classif = holdToken.classif;
                        Assign();
                        read(")");
                        Statement();
                        buildTree("for",4,false);
                    }
                }
                else
                {
                    Expression();
                    read(")");
                    Statement();
                    buildTree("for",4,false);
                }
            
            }
            else if(tokenItem.classif.equals("switch"))
            {
                int n=1;
                read("switch");
                read("(");
                Expression();
                read(")");
                read("{");
                do
                {
                CaseClause();
                n++;
                }
                while(tokenItem.classif.equals("case"));
                if(tokenItem.classif.equals("default"))
                {
                    n++;
                DefaultClause();
                }
                buildTree("switch",n,false);
            }
            else if(tokenItem.classif.equals("break"))
            {
                read("break");
                read(";");
                buildTree("break",0,false);
            }
            else if(tokenItem.classif.equals("continue"))
            {
                read("continue");
                read(";");
                buildTree("continue",0,false);
            }
            else if(tokenItem.classif.equals("returnx"))
            {
                read("returnx");
                if(tokenItem.classif.equals(";"))
                {
                    read(";");
                    buildTree("return",0,false);
                }
                else
                {
                    Expression();
                    read(";");
                    buildTree("return",1,false);
                }
            }
            else if(tokenItem.classif.equals(";"))
            {
                Null();
                read(";");
            }
            else if(tokenItem.classif.equals("const"))
            {
                    Var();
                    read(";");
            }
            else if(tokenItem.classif.equals("identifier"))
            {
               
                TokenItem tokenHold = new TokenItem(tokenItem.classif,tokenItem.data);
                int holdpos = count;
                scanner();
                if(tokenItem.classif.equals("identifier"))
                {
                    tokenItem.data = tokenHold.data;
                    tokenItem.classif = tokenHold.classif;
                    count = holdpos;
                    Var();
                    read(";");
                }
                else
                {
                    if(checkAssign(false))
                    {
                
                        tokenItem.data = tokenHold.data;
                        tokenItem.classif = tokenHold.classif;
                        count = holdpos;
                        Assign();
                        read(";");
                    }
                    else
                    {
                        tokenItem.data = tokenHold.data;
                        tokenItem.classif = tokenHold.classif;
                        count = holdpos;
                        Expression();
                        read(";");
                    }
                }
                    
                
            }
            else if(tokenItem.classif.equals("EOF"))
            {
            }
            
            else
            {
                System.out.println("STATEMENT ERROR " + tokenItem.data + tokenItem.classif);
                scanner();
            }
                    
          }
        public void FuncCall()
        {
            Name();
            read("(");
            if(tokenItem.classif.equals(")"))
            {
                read(")");
                buildTree("call",1,false);
            }
            else
            {
                Params();
                read(")");
                buildTree("call",2,false);
            }
          }
        public void Params()
        {
            int n = 0;
            do
            {
                Param();
                n++;
                if(tokenItem.classif.equals(","))
                read(",");
            }
            while(tokenItem.classif.equals("identifier")||tokenItem.classif.equals("integer")||tokenItem.classif.equals("char")||tokenItem.classif.equals("float")||tokenItem.classif.equals("String")||tokenItem.classif.equals("("));
            buildTree("params",n,false);
        }
        public void Codeblock()
        {
            int n =0;
            read("{");
            while(!tokenItem.classif.equals("}"))
            {
                Statement();
                n++;
            }
            read("}");
            buildTree("block",n,false);
        }
        public void Param()
        {
            TokenItem holdToken = new TokenItem(tokenItem.classif,tokenItem.data);
            int holdpos = count;
            if(checkAssign(true))
            {
                
                count = holdpos;
                tokenItem.data = holdToken.data;
                tokenItem.classif = holdToken.classif;
                Assign();
            }
            else
            {
                
                count = holdpos;
                tokenItem.data = holdToken.data;
                tokenItem.classif = holdToken.classif;
                Expression();
                
            }
        }
        public void CaseClause()
        {
            int n = 1;
            read("case");
            Expression();
            read(":");
            while(checkStatement())
            {
                Statement();
                n++;
            }
            buildTree("case",n,false);
        }
        public void DefaultClause()
        {
            int n = 0;
            read("default");
            read(":");
            while(checkStatement())
            {
                Statement();
                n++;
            }
            buildTree("default",n,false);
        }
        public void Assign()
        {
            Element();
            if(tokenItem.classif.equals("="))
            {
                read("=");
                Param();
                buildTree("=",2,false);
            }
            else if(tokenItem.classif.equals("PEQ"))
            {
                read("PEQ");
                Param();
                buildTree("+=",2,false);
            }
            else if(tokenItem.classif.equals("SEQ"))
            {
                read("SEQ");
                Param();
                buildTree("-=",2,false);
            }
            else if(tokenItem.classif.equals("TEQ"))
            {
                read("TEQ");
                Param();
                buildTree("*=",2,false);
            }
            else if(tokenItem.classif.equals("DEQ"))
            {
                read("DEQ");
                Param();
                buildTree("/=",2,false);
            }
            else if(tokenItem.classif.equals("MEQ"))
            {
                read("MEQ");
                Param();
                buildTree("%=",2,false);
            }
            else if(tokenItem.classif.equals("AEQ"))
            {
                read("AEQ");
                Param();
                buildTree("&=",2,false);
            }
            else if(tokenItem.classif.equals("OEQ"))
            {
                read("OEQ");
                Param();
                buildTree("|=",2,false);
            }
            else if(tokenItem.classif.equals("XEQ"))
            {
                read("XEQ");
                Param();
                buildTree("^=",2,false);
            }
        }
        public void Var()
        {
            if(tokenItem.classif.equals("const"))
            {
                read("const");
                Name();
                Varlist();
                buildTree("const",2,false);
            }
            else
            {
                Name();
                Varlist();
                buildTree("var",2,false);
            }
                
        }
        public void Varlist()
        {
            do
            {
                TokenItem tokenHold = new TokenItem(tokenItem.classif,tokenItem.data);
                int holdpos = count;
                if(checkAssign(true)) //returns true if at element, false if at assign
                {
                    count = holdpos;
                    tokenItem.data = tokenHold.data;
                    tokenItem.classif = tokenHold.classif;
                    
                    Assign();
                    if(tokenItem.classif.equals(","))
                    read(",");
                }
                else
                {
                   
                    count = holdpos;
                    tokenItem.data = tokenHold.data;
                    tokenItem.classif = tokenHold.classif;
                    Element();
                    if(tokenItem.classif.equals(","))
                    read(",");
                }
                                     
            }while(tokenItem.classif.equals("identifier"));
        }
        public void Element()
        {
            Name();
            if(tokenItem.classif.equals("["))
            {
                read("[");
                Expression();
                read("]");
                buildTree("[]",2,false);
            }
        }
        public void Expression()
        {
            
            Exp1();
            if(tokenItem.classif.equals("?"))
            {
                read("?");
                Exp1();
                read(":");
                Exp1();
                buildTree("?:",3,false);
            }
        }
        public void Exp1()
        {
            Exp2();
            if(tokenItem.classif.equals("LOR"))
                {
                    read("LOR");
                    Exp1();
                    buildTree("||",2,false);
                }
        }
        public void Exp2()
        {
            Exp3();
            if(tokenItem.classif.equals("LAND"))
            {
                read("LAND");
                Exp2();
                buildTree("&&",2,false);
            }
        }
        public void Exp3()
        {
            Exp4();
            if(tokenItem.classif.equals("|"))
            {
                read("|");
                Exp3();
                buildTree("|",2,false);
            }
        }
        public void Exp4()
        {
            Exp5();
            if(tokenItem.classif.equals("^"))
            {
                read("^");
                Exp4();
                buildTree("^",2,false);
            }
        }
        public void Exp5()
        {
            Exp6();
            if(tokenItem.classif.equals("&"))
            {
                read("&");
                Exp5();
                buildTree("&",2,false);
            }
        }
        public void Exp6()
        {
            Exp7();
            if(tokenItem.classif.equals("EQ"))
            {
                read("EQ");
                Exp6();
                buildTree("==",2,false);
            }
            else if(tokenItem.classif.equals("NE"))
            {
                read("NE");
                Exp6();
                buildTree("!=",2,false);
            }
        }
            public void Exp7()
            {
            Exp8();
            if(tokenItem.classif.equals("<"))
            {
                read("<");
                Exp7();
                buildTree("<",2,false);
                        
            }
            else if(tokenItem.classif.equals(">"))
            {
                read(">");
                Exp7();
                buildTree(">",2,false);
                        
            }
            else if(tokenItem.classif.equals("LTE"))
            {
                read("LTE");
                Exp7();
                buildTree("<=",2,false);
                        
            }
            else if(tokenItem.classif.equals("GTE"))
            {
                read("GTE");
                Exp7();
                buildTree(">=",2,false);
                        
            }
        }
            public void Exp8()
            {
                Exp9();
                if(tokenItem.classif.equals("+"))
                {
                    read("+");
                    Exp8();
                    buildTree("+",2,false);
                        
                }
                else if(tokenItem.classif.equals("-"))
                {
                    read("-");
                    Exp8();
                    buildTree("-",2,false);
                        
                }
            }
            public void Exp9()
            {
                Exp10();
                if(tokenItem.classif.equals("*"))
                {
                    read("*");
                    Exp9();
                    buildTree("*",2,false);
                        
                }
                else if(tokenItem.classif.equals("/"))
                {
                    read("/");
                    Exp9();
                    buildTree("/",2,false);
                        
                }
                else if(tokenItem.classif.equals("%"))
                {
                    read("%");
                    Exp9();
                    buildTree("%",2,false);
                        
                }
            }
            public void Exp10()
            {
                if(tokenItem.classif.equals("!"))
                {
                    read("!");
                    Exp11();
                    buildTree("!",1,false);
                            
                }
                else if(tokenItem.classif.equals("INC"))
                {
                    read("INC");
                    Exp11();
                    buildTree("++",1,false);
                }
                else if(tokenItem.classif.equals("DEC"))
                {
                    read("DEC");
                    Exp11();
                    buildTree("--",1,false);
                }
                else if(tokenItem.classif.equals("+"))
                {
                    read("+");
                    Exp11();
                    buildTree("+",1,false);
                }
                else if(tokenItem.classif.equals("-"))
                {
                    read("-");
                    Exp11();
                    buildTree("-",1,false);
                }
                else
                {
                    Exp11();
                    if(tokenItem.classif.equals("INC"))
                    {
                        read("INC");
                        Null();
                        buildTree("++",2,false);
                    }
                    else if(tokenItem.classif.equals("DEC"))
                    {
                        read("DEC");
                        Null();
                        buildTree("--",2,false);
                    }
                    
                }
                
            }
            public void Exp11()
            {
                if(tokenItem.classif.equals("("))
                {
                    read("(");
                    Name();
                    read(")");
                    Exp12();
                    buildTree("<cast>",2,false);
                }
                else
                {
                    Exp12();
                }
            }
            public void Exp12()
            {
                if(tokenItem.classif.equals("integer"))
                {
                    Integer();
                }
                if(tokenItem.classif.equals("float"))
                {
                    Float();
                }
                if(tokenItem.classif.equals("char"))
                {
                    Char();
                }
                if(tokenItem.classif.equals("String"))
                {
                    String();
                }
                
                if(tokenItem.classif.equals("identifier"))
                {
                    TokenItem holdToken = new TokenItem(tokenItem.classif,tokenItem.data);
                    int holdpos = count;
                    scanner(); // get past Identifier
                    if(tokenItem.classif.equals("("))
                    {
                        tokenItem.data = holdToken.data;
                        tokenItem.classif = holdToken.classif;
                        count = holdpos;
                        FuncCall();
                    }
                    else
                    {
                        tokenItem.data = holdToken.data;
                        tokenItem.classif = holdToken.classif;
                        count = holdpos;
                        Element();
                    }                
                }
                else if(tokenItem.classif.equals("("))
                {
                    read("(");
                    TokenItem holdToken = new TokenItem(tokenItem.classif,tokenItem.data);
                    int holdpos = count;
                    if(checkAssign(true))
                    {
                        tokenItem.data = holdToken.data;
                        tokenItem.classif = holdToken.classif;
                        count = holdpos;
                        Assign();
                    }
                    else
                    {
                        tokenItem.data = holdToken.data;
                        tokenItem.classif = holdToken.classif;
                        count = holdpos;
                        Expression();
                    }
                    read(")");
                }
            }
            public void Integer()
            {
                
                buildTree(tokenItem.data,0,false);
                scanner();
                buildTree("<integer>",1,false);
                
            }
            public void Null()
            { 
                buildTree("<null>",0,false);
            }
            public void Name()
            {
                buildTree(tokenItem.data,0,false);
                scanner();
                buildTree("<identifier>",1,false);
             }
            public void Char()
            {
                buildTree(tokenItem.data,0,false);
                scanner();
                buildTree("<char>",1,false);
            }
            public void Float()
            {
                 
                buildTree(tokenItem.data,0,false);
                scanner();
                buildTree("<float>",1,false);
            }
            public void String()
            {
                buildTree(tokenItem.data,0,false);
                scanner();
                buildTree("<string>",1,false);
            }
            public boolean checkAssign(boolean push)
            {
                    if(push)
                    scanner(); //push past name
                    if(tokenItem.classif.equals("["))
                    {
                        scanner(); //push past [
                        int extra =0;
                        while(!tokenItem.classif.equals("]")&& extra ==0)
                        {
                            if(tokenItem.classif.equals("[")) //handle additional brackets
                            {
                                extra++;
                            }
                            if(tokenItem.classif.equals("]"))
                            {
                                extra--;
                            }
                            scanner(); //push past Expression
                        }
                        scanner(); //push past ]
                    }
                    if(checkEvaluator())
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
            }
            public boolean checkElementVsAssign() //returns true if at element, false if at assign
            {
            int holdposition = count;
           TokenItem tokenItemHold = tokenItem;
            scanner();
            if(tokenItem.classif.equals("["))
            {
                scanner(); // push past [
                scanner(); // push past Expression
                scanner();// push past ]
            }
            if(tokenItem.classif.equals("=")||tokenItem.classif.equals("PEQ")||tokenItem.classif.equals("SEQ")||tokenItem.classif.equals("TEQ")||tokenItem.classif.equals("DEQ")||tokenItem.classif.equals("MEQ")||tokenItem.classif.equals("AEQ")||tokenItem.classif.equals("OEQ")||tokenItem.classif.equals("XEQ"))
            {
                tokenItem = tokenItemHold;
                count = holdposition;
                return false;
            }
            else
            {
                tokenItem = tokenItemHold;
                count = holdposition;
                return true;   
            }
        }
            public boolean checkStatement()
            {
            if(tokenItem.classif.equals("identifier")||checkExpression()||tokenItem.classif.equals("")||tokenItem.classif.equals("if")||tokenItem.classif.equals("{")||tokenItem.classif.equals("printf")||tokenItem.classif.equals("scanf")||tokenItem.classif.equals("while")||tokenItem.classif.equals("do")||tokenItem.classif.equals("for")||tokenItem.classif.equals("switch")||tokenItem.classif.equals("break")||tokenItem.classif.equals("continue")||tokenItem.classif.equals("returnx")||tokenItem.classif.equals(";"))
                return true;
            else
                return false;
        }
            public boolean checkExpression()
            {
            if(tokenItem.classif.equals("identifier")||tokenItem.classif.equals("integer")||tokenItem.classif.equals("char")||tokenItem.classif.equals("float")||tokenItem.classif.equals("String")||tokenItem.classif.equals("("))
                return true;
            else
                return false;
            
        }
            public boolean checkEvaluator()
            {
                if(tokenItem.classif.equals("=")||tokenItem.classif.equals("PEQ")||tokenItem.classif.equals("SEQ")||tokenItem.classif.equals("TEQ")||tokenItem.classif.equals("DEQ")||tokenItem.classif.equals("MEQ")||tokenItem.classif.equals("AEQ")||tokenItem.classif.equals("AEQ")||tokenItem.classif.equals("OEQ")||tokenItem.classif.equals("XEQ"))
               {
                    return true;
                }
                else
                {
                    return false;
                }
                
            }
            public void scanner()
            {
     prevscancount = count;
     String data = "";
     String aps ="'";
     char apos = aps.charAt(0);
     if(Character.isDigit(chars[count]))
     {
         
         while(Character.isDigit(chars[count]))
         {
             data = data+chars[count];
             count++;
         }
         if(chars[count] == '.')
         {
             data = data+chars[count];
             count++;
             while(Character.isDigit(chars[count]))
             {
                 data = data+chars[count];
             }
              tokenItem.data = data;
              tokenItem.classif = "float";
         }
         else
         {          
            tokenItem.data = data;
            tokenItem.classif = "integer";
         }
     }
     else if(Character.isLetter(chars[count]))
     {
         while(Character.isLetter(chars[count]) || Character.isDigit(chars[count]))
         {
             data = data + chars[count];
             count++;
         }
         if(data != null)
         tokenItem.data = data;
         tokenItem.classif = "identifier";
         try{
         switch(funcList.valueOf(data.toUpperCase()))
         {
             case CONTINUE:
                 tokenItem.classif ="continue";
                 break;
             case TYPEDEF:
                 tokenItem.classif ="typedef";
                 break;
             case DEFAULT:
                 tokenItem.classif ="default";
                 break;
             case RETURN:
                 tokenItem.classif ="returnx";
                 break;
             case SWITCH:
                 tokenItem.classif ="switch";
                 break;
             case PRINTF:
                 tokenItem.classif ="printf";
                 break;
             case BREAK:
                 tokenItem.classif ="break";
                 break;
             case WHILE:
                 tokenItem.classif ="while";
                 break;
             case SCANF:
                 tokenItem.classif ="scanf";
                 break;
             case CONST:
                 tokenItem.classif ="const";
                 break;
             case CASE:
                 tokenItem.classif ="case";
                 break;
             case ELSE:
                 tokenItem.classif ="else";
                 break;
             case FOR:
                 tokenItem.classif ="for";
                 break;
             case IF:
                 tokenItem.classif ="if";
                 break;
             case DO:
                 tokenItem.classif ="do";
                 break;
             case EOF:
                 tokenItem.classif = "EOF";
                 break;
             default:                 
             tokenItem.classif = "identifier";
             break;
         }
         }
         catch(IllegalArgumentException e){}
     }
     
     else if(chars[count] == '"')
     {
         count++;
                 while(chars[count]!='"')
                 {
                     data = data+ chars[count];
                     count++;
                 }
         count++;
         tokenItem.data = data;
         tokenItem.classif = "String";
     }
     else if(chars[count] == apos)
     {
         count++;
                 while(chars[count]!=apos)
                 {
                     data = data+ chars[count];
                     count++;
                 }
         count++;
         tokenItem.data = data;
         tokenItem.classif = "char";
     }
     else if(Character.isSpaceChar(chars[count]))
     {
         count++;
         scanner();         
     }
    else
    {
        switch(chars[count])
        {
            case '+':
            
                data = data + chars[count];
                if(chars[count+1]=='=')
                {
                    count++;
                    data = data+chars[count];
                    tokenItem.classif="PEQ";
                    tokenItem.data=data;
                    count++;
                }
                else if(chars[count+1]=='+')
                {
                    count++;
                    data = data+chars[count];
                    tokenItem.classif="INC";
                    tokenItem.data=data;
                    count++;
                }
                else
                {
               tokenItem.classif=data;
               tokenItem.data=data;
               count++;
                }
               break;
            
            case '-':
            
                data = data + chars[count];
                if(chars[count+1] == '=')
                {
                    count++;
                    data = data + chars[count];
                    tokenItem.classif = "SEQ";
                    tokenItem.data = data;
                    count++;
                }
                else if(chars[count+1] == '-')
                {
                    count++;
                    data = data+chars[count];
                    tokenItem.classif = "DEC";
                    tokenItem.data = data;
                    count++;
                }
                else
                {
                 tokenItem.classif = data;
                 tokenItem.data = data;
                 count++;
                }
                break;
            case '*':
            
                data = data + chars[count];
                if(chars[count+1]=='=')
                {
                    count++;
                    data = data + chars[count];
                    tokenItem.classif = "TEQ";
                    tokenItem.data = data;
                    count++;
                }
                else
                {
                    tokenItem.data = data;
                    tokenItem.classif = data;
                    count++;
                }
                break;
                case '%':
                    data = data + chars[count];
                    if(chars[count+1] == '=')
                    {
                        count++;
                        data = data + chars[count];
                        tokenItem.classif = "MEQ";
                        tokenItem.data = data;
                        count++;
                    }
                    else
                    {
                        tokenItem.data = data;
                        tokenItem.classif = data;
                        count++;
                    }
                    break;
            case '&':
                data = data + chars[count];
                if(chars[count+1] == '&')
                {
                    count++;
                    data = data+chars[count];
                    tokenItem.classif = "LAND";
                    tokenItem.data = data;
                    count++;
                }
                else if(chars[count+1] == '=')
                {
                   
                    count++;
                    data = data+chars[count];
                    tokenItem.classif = "AEQ";
                    tokenItem.data = data;
                    count++;
                }
                else
                {
                    tokenItem.data = data;
                    tokenItem.classif = data;
                    count++;
                }
                break;
            case '^':
                data = data+ chars[count];
                if(chars[count+1]== '=')
                {
                    count++;
                    data = data+chars[count];
                    tokenItem.classif = "XEQ";
                    tokenItem.data = data;
                    count++;
                }
                else
                {
                    tokenItem.data=data;
                    tokenItem.classif=data;
                    count++;
                }
                break;
            case '|':
                data = data + chars[count];
                if(chars[count+1]== '=') 
                {
                    count++;
                    data = data+chars[count];
                    tokenItem.classif = "OEQ";
                    tokenItem.data = data;
                    count++;
                }
                else if(chars[count+1] == '|')
                {
                    count++;
                    data = data+chars[count];
                    tokenItem.classif = "LOR";
                    tokenItem.data = data;
                    count++;
                }
                else
                {                    
                    tokenItem.data=data;
                    tokenItem.classif=data;
                    count++;
                }
                break;
            case '<':
                data = data+chars[count];
                if(chars[count+1]=='=')
                {
                    count++;
                    data = data+chars[count];
                    tokenItem.classif = "LTE";
                    tokenItem.data = data;
                    count++;
                }
                else
                {
                    tokenItem.classif = data;
                    tokenItem.data = data;
                    count++;
                }
                break;
            case'>':
                data = data+chars[count];
                if(chars[count+1] == '=')
                {
                    count++;
                    data = data+chars[count];
                    tokenItem.classif = "GTE";
                    tokenItem.data = data;
                    count++;
                }
                else
                {
                    tokenItem.classif=data;
                    tokenItem.data=data;
                    count++;
                }
                break;
            case'!':
                data = data+chars[count];
                if(chars[count+1]=='=')
                {
                    count++;
                    data = data+chars[count];
                    tokenItem.classif = "NE";
                    tokenItem.data = data;
                    count++;
                }
                else
                {
                    tokenItem.classif=data;
                    tokenItem.data=data;
                    count++;
                }
                break;
            case'=':
                data=data+chars[count];
                if(chars[count+1]=='=')
                {
                    count++;
                    data = data+chars[count];
                    tokenItem.classif = "EQ";
                    tokenItem.data = data;
                    count++;
                }
                else
                {
                    tokenItem.data=data;
                    tokenItem.classif=data;
                    count++;
                }
                break;
            case';':
                tokenItem.data="" +';';
                tokenItem.classif="" +';';
                count++;
                break;
            case',':
                tokenItem.data="" +',';
                tokenItem.classif="" +',';
                count++;
                break;
            case':':
                tokenItem.data = "" +':';
                tokenItem.classif="" +':';
                count++;
                break;
            case'?':
                tokenItem.data=""+'?';
                tokenItem.classif=""+'?';
                count++;
                break;
            case'[':
                tokenItem.data="" +'[';
                tokenItem.classif=""+'[';
                count++;
                break;
            case']':
                tokenItem.data="" +']';
                tokenItem.classif=""+']';
                count++;
                break;
            case'/':
                if(chars[count+1]=='*')
                {
                    
                    boolean breakout = false;
                    count++;
                    count++;
                    while(!breakout)
                    {
                        if(chars[count]=='*')
                        {
                            count++;
                            if(chars[count]=='/')
                            {
                                breakout=true;
                            }
                           
                        }
                        count++;
                    }
                    scanner();
                }
                else if(chars[count+1] == 't')
                {
                    count++;
                    count++;
                    scanner();
                }
                else if(chars[count+1] == 'n')
                {
                    count++;
                    count++;
                    scanner();
                }
                else if(chars[count+1] == '/')
                {
                    count++;
                    count++;
                    boolean breakout = false;
                    while(!breakout)
                    {
                        if(chars[count]=='/')
                        {
                            count++;
                            if(chars[count]=='n')
                            {
                                
                                breakout=true;
                            }
                           
                        }
                        
                        count++;
                        
                    }
                    scanner();
                }
                else if(chars[count+1]=='=')
                {
                    count++;
                    data = data+chars[count];
                    tokenItem.data = data;
                    tokenItem.classif ="DEQ";
                    count++;
                }
                else
                {
                    tokenItem.data = ""+chars[count];
                    tokenItem.classif = "" +chars[count];
                    count++;
                }
                break;
            case '{':
                tokenItem.data = "" + '{';
                tokenItem.classif = "" + '{';
                count++;
                break;
            case '}':
                tokenItem.data = "" + '}';
                tokenItem.classif = "" + '}';
                count++;
                break;
            case '(':
                tokenItem.data = "" + '(';
                tokenItem.classif = "" + '(';
                count++;
                break;
            case ')':
                tokenItem.data = "" + ')';
                tokenItem.classif = "" + ')';
                count++;
                break;
            case ' ':
                count++;
                scanner();
                break;
             
                
            default:
                System.out.print("Unrecognised character " + chars[count]);
                        count++;
                scanner();
                break;
                
           }
         }
        }
}

     
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       String choice = "";
       System.out.println("You must enter COMPLETE path to file in order for is to be read.");
       System.out.print("Input File Name:");
        try{
        BufferedReader userInput;
        userInput = new BufferedReader(new InputStreamReader(System.in));
        choice = userInput.readLine();
        userInput.close();
       }
       catch(IOException e)
       {}
       Parser pars = new Parser(choice);
       BinaryTreeNode h = (BinaryTreeNode) pars.mainStack.pop();
       BinaryTree Final = new BinaryTree(h);
       Final.print();
       }
      }


