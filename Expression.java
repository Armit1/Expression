package app;

import structures.Stack;

import java.io.*;
import java.util.*;

public class Expression {

	public static String delims = " \t*+-/()[]";

	/**
	 * Populates the vars list with simple variables, and arrays lists with
	 * arrays in the expression. For every variable (simple or array), a SINGLE
	 * instance is created and stored, even if it appears more than once in the
	 * expression. At this time, values for all variables and all array items
	 * are set to zero - they will be loaded from a file in the
	 * loadVariableValues method.
	 *
	 * @param expr
	 *            The expression
	 * @param vars
	 *            The variables array list - already created by the caller
	 * @param arrays
	 *            The arrays array list - already created by the caller
	 */
	public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		for (int i = 0; i < expr.length(); i++) {
			if (Character.isLetter(expr.charAt(i))) {
				String var = "";
				while (i < expr.length() && Character.isLetter(expr.charAt(i))) {
					var += expr.charAt(i);
					i++;
				}
				if (i < expr.length() && expr.charAt(i) == '[') {
					arrays.add(new Array(var));
				} else {
					vars.add(new Variable(var));
				}
			}
		}
	}

	/**
	 * Loads values for variables and arrays in the expression
	 *
	 * @param sc
	 *            Scanner for values input
	 * @param vars
	 *            The variables array list, previously populated by
	 *            makeVariableLists
	 * @param arrays
	 *            The arrays array list - previously populated by
	 *            makeVariableLists
	 * @throws IOException
	 *             If there is a problem with the input
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
				// System.out.println(vari);
				vars.get(vari).value = num;
			} else { // array symbol
				arr = arrays.get(arri);
				arr.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()) {
					tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok, " (,)");
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
	 * @param vars
	 *            The variables array list, with values for all variables in the
	 *            expression
	 * @param arrays
	 *            The arrays array list, with values for all array items
	 * @return Result of evaluation
	 */
	public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		String exp = expr;
		if (expr.contains("[")) {
			exp = exp.replaceAll("[\\t]", "");
			int length = exp.length();
			int last = -1;
			// -------------------------------------------------
			for (int i = 0; i < length; i++) {
				char c = exp.charAt(i);
				if (c == '[') {
					String name = exp.substring(last + 1, i);
					int arr = arrays.indexOf(new Array(name));
					int close = 0, skip = 1;
					int count = i + 1;
					while (skip > 0) {
						char test = exp.charAt(count);
						if (test == '[')
							skip++;
						else if (test == ']')
							skip--;
						if (skip == 0) {
							close = count;
							break;
						}
						count++;
					}
					// -------------------------------------------------
					String subexp = exp.substring(i + 1, close);
					int index = (int) evaluate(subexp, vars, arrays);
					String result = arrays.get(arr).values[index] + "";
					return evaluate(exp.replace(name + "[" + subexp + "]", result), vars, arrays);
				}
				if (delims.contains(c + "")) {
					last = i;
				}
			}
			return evaluate(exp, vars, arrays);
		} else {
			String exp2 = expr;
			exp2 = exp2.replaceAll("[ \\t]", "");
			String ops = "()/*+-";
			Stack<Float> nums = new Stack<>();
			Stack<Character> operators = new Stack<>();
			// -------------------------------------------------
			int length = exp2.length();
			int last = -1;
			for (int i = 0; i < length + 1; i++) {

				if (i == length) {
					if (exp2.charAt(i - 1) != ')') {
						String name = exp2.substring(last + 1);
						int varIndex = vars.indexOf(new Variable(name));
						if (varIndex >= 0) {
							Variable testicle = vars.get(varIndex);
							nums.push((float) (testicle.value));
						} else {
							nums.push(Float.parseFloat(name));
						}
					}
					break;
				}
				// -------------------------------------------------
				char c = exp2.charAt(i);
				if (ops.contains(c + "")) {
					String name = exp2.substring(last + 1, i);
					if (!name.equals("")) {
						int var = vars.indexOf(new Variable(name));
						if (var >= 0) {
							Variable testicle2 = vars.get(var);
							nums.push((float) (testicle2.value));
						} else {
							nums.push(Float.parseFloat(name));
						}
					}

					if (c == '(') {
						operators.push(c);
					} else if (c == ')') {
						while (operators.peek() != '(') {
							eval(nums, operators);
						}
						operators.pop();
					} else {
						while (!operators.isEmpty() && precFinder(operators.peek(), c) == 1) {
							eval(nums, operators);
						}
						operators.push(c);
					}
					last = i;
				}
			}
			while (!operators.isEmpty()) {
				eval(nums, operators);
			}
			return nums.peek();
		}
	}

	private static void eval(Stack<Float> nums, Stack<Character> operators) {
		float right = nums.pop();
		float left = nums.pop();
		char operator = operators.pop();
		float result = 0;
		if (operator == '+') {
			result = left + right;
		}
		if (operator == '-') {
			result = left - right;
		}
		if (operator == '*') {
			result = left * right;
		} else
			result = left / right;
		nums.push(result);
	}

	private static int precFinder(char a, char b) {
		if ((a == '*' || a == '/') && (b != '*' || b != '/')) {
			return 1;
		}
		if ((a != '*' || a != '/') && (b == '*' || b == '/')) {
			return -1;
		} else
			return 0;
		// boolean yeet;
	}

}
