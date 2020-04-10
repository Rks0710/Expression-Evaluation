package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /*
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created 
     * @param arrays The arrays array list - already created 
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	// This is where Two arraylists are being populated 
    	 	String newexpr = expr.replaceAll("\\s+", "");
		for (int i = 0; i < newexpr.length(); i++) {
			String temp = "";
			while (i < newexpr.length() && Character.isLetter(newexpr.charAt(i))) {
				temp += newexpr.charAt(i);
				i++;
			}
			if (i < newexpr.length()) {
				if (newexpr.charAt(i) == '[') {
					Array b = new Array(temp);
					if (!arrays.contains(b)) {
						arrays.add(b);
					}
				} else {
					Variable b = new Variable(temp);
					vars.add(b);
				}
			} else {
				Variable b = new Variable(temp);
				vars.add(b);
			}

		}
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    
public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	String newexpr = expr.replaceAll("\\s+", "");
		int result = 0;
		Stack<Float> operand = new Stack<>();
		Stack<Character> operator = new Stack<>();
		if (newexpr.length() == 1) {
			if (Character.isDigit(newexpr.charAt(0))) {
				result = Character.getNumericValue(newexpr.charAt(0));
				return result;
			}
			if (Character.isLetter(newexpr.charAt(0))) {
				result = vars.get(0).value;
				return result;
			}
		}

		else {
			for (int i = 0; i < newexpr.length(); i++) {
				//This is where I figure out whether we have parentheses or not in the expression
				if (newexpr.charAt(i) == ('(')) {
					int count = 1;
					int tempStart = i;
					i++;
					while (count != 0) {
						if (newexpr.charAt(i) == ('(')) {
							count++;
						}
						if (newexpr.charAt(i) == (')')) {
							count--;
						}
						i++;

					}
					String str = newexpr.substring(tempStart + 1, i - 1);
					//This is where Recursion Happens with the expression inside the parentheses
					result = (int) evaluate(str, vars, arrays);
					System.out.println(result + "dfd");
					operand.push((float) result);
				}

				String Num = "";
				while (i < newexpr.length() && Character.isDigit(newexpr.charAt(i))) {
					Num = Num + newexpr.charAt(i);
					i++;
				}
				if (Num != "") {
					operand.push(Float.parseFloat(Num));
				}

				String letter = "";
				int position = 0;
				while (i < newexpr.length() && Character.isLetter(newexpr.charAt(i))) {
					letter = letter + newexpr.charAt(i);
					i++;

					if (i < newexpr.length() && !Character.isLetter(newexpr.charAt(i)) && newexpr.charAt(i) != '['
							|| i >= newexpr.length()) {
						for (int a = 0; a < vars.size(); a++) {
							if (vars.get(a).name.equals(letter)) {
								position = a;
								break;
							}
						}

						operand.push((float) vars.get(position).value);

					}

					else if (i < newexpr.length() && newexpr.charAt(i) == '[') {
						if (newexpr.charAt(i) == ('[')) {
							int count1 = 1;
							int tempStart = i;

							i++;
							while (count1 != 0) {
								if (newexpr.charAt(i) == ('[')) {
									count1++;
								}
								if (newexpr.charAt(i) == (']')) {
									count1--;
								}
								i++;

							}
							//Second Recursion for bigger Subbrackets
							result = (int) evaluate(newexpr.substring(tempStart + 1, i - 1), vars, arrays);
							int arrindex = arrays.indexOf(new Array(letter));
							operand.push((float) arrays.get(arrindex).values[result]);

						}
					}
				}

				if (i < newexpr.length() && (newexpr.charAt(i) == '+' || newexpr.charAt(i) == '-'
						|| newexpr.charAt(i) == '*' || newexpr.charAt(i) == '/')) {


					while (!operator.isEmpty() && Precedence(operator.peek(), newexpr.charAt(i))) {
						char sign = operator.pop();
						if (sign == '-') {
							float secondNum = operand.pop();
							float firstNum = operand.pop();
							operand.push(firstNum - secondNum);
						}
						if (sign == '+') {
							float secondNum = operand.pop();
							float firstNum = operand.pop();
							operand.push(firstNum + secondNum);
						}
						if (sign == '*') {
							float secondNum = operand.pop();
							float firstNum = operand.pop();
							operand.push(firstNum * secondNum);
						}
						if (sign == '/') {
							float secondNum = operand.pop();
							float firstNum = operand.pop();
							operand.push(firstNum / secondNum);
						}
					}
					operator.push(newexpr.charAt(i));
				}
			}
			//This loop does the final evauation of all results from the substrings that we got using recursion
			while (!operator.isEmpty()) {
				char sign = operator.pop();
				if (sign == '-') {
					float secondNum = operand.pop();
					float firstNum = operand.pop();
					operand.push(firstNum - secondNum);
				}
				if (sign == '+') {
					float secondNum = operand.pop();
					float firstNum = operand.pop();
					operand.push(firstNum + secondNum);
				}
				if (sign == '*') {
					float secondNum = operand.pop();
					float firstNum = operand.pop();
					operand.push(firstNum * secondNum);
				}
				if (sign == '/') {
					float secondNum = operand.pop();
					float firstNum = operand.pop();
					operand.push(firstNum / secondNum);
				}
			}
		}

		return operand.pop();
    }
	
	private static boolean Precedence(char a, char b) {
		if ((a == '+' && b == '*') || (a == '+' && b == '/') || (a == '-' && b == '/') || (a == '-' && b == '*')) {
			return false;
		}
		if ((a == '+' && b == '-') || (a == '-' && b == '-') || (a == '+' && b == '+') || (a == '-' && b == '+')) {
			return true;
		}
		if ((a == '*' && b == '/') || (a == '/' && b == '*') || (a == '*' && b == '*') || (a == '/' && b == '/')) {
			return true;
		}
    	
		return true;
	}
}
 
