package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression2 {

	public static String delims = " \t*+-/()[]";

	/**
	 * Populates the vars list with simple variables, and arrays lists with
	 * arrays in the expr. For every variable (simple or array), a SINGLE
	 * instance is created and stored, even if it appears more than once in the
	 * expr. At this time, values for all variables and all array items are set
	 * to zero - they will be loaded from a file in the loadVariableValues
	 * method.
	 * 
	 * @param expr
	 *            The expr
	 * @param vars
	 *            The variables array list - already created by the caller
	 * @param arrays
	 *            The arrays array list - already created by the caller
	 */
	public static boolean isOnlySpace(String s) {
		boolean hasVars = true;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != ' ') {
				hasVars = false;
			}
		}
		return hasVars;
	}

	public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		/** COMPLETE THIS METHOD **/
		/**
		 * DO NOT create new vars and arrays - they are already created before
		 * being sent in to this method - you just need to fill them in.
		 **/

		for (int i = 0; i < expr.length(); i++) {
			if (Character.isLetter(expr.charAt(i))) {
				String var = "";
				while (i < expr.length() && Character.isLetter(expr.charAt(i))) {
					var += expr.charAt(i);
					i++;
				}
				if (i < expr.length() && expr.charAt(i) == '[') {
					vars.add(new Variable(var));
				} else {
					arrays.add(new Array(var));
				}
			}
		}
		/*
		 * String delimiter = "*-+/"; String temp = expr; String exp = "";
		 * String[] arr; for (int i = 0; i < temp.length(); i++) { String del =
		 * temp.charAt(i) + ""; arr = temp.split(del); exp = ""; for (String s :
		 * arr) { exp += s; }
		 * 
		 * }
		 */

	}

	/**
	 * Loads values for variables and arrays in the expr
	 * 
	 * @param sc
	 *            Scanner for values input
	 * @throws IOException
	 *             If there is a problem with the input
	 * @param vars
	 *            The variables array list, previously populated by
	 *            makeVariableLists
	 * @param arrays
	 *            The arrays array list - previously populated by
	 *            makeVariableLists
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
					StringTokenizer stt = new StringTokenizer(tok, " (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					arr.values[index] = val;
				}
			}
		}
	}

	/**
	 * Evaluates the expr.
	 * 
	 * @param vars
	 *            The variables array list, with values for all variables in the
	 *            expr
	 * @param arrays
	 *            The arrays array list, with values for all array items
	 * @return Result of evaluation
	 */

	public static String evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		if (expr == null || expr.length() == 0) {
			return "0";
		}

		if (expr.indexOf('[') == -1) {
			if (expr.indexOf('(') == -1) {
				expr = expr.replace(" ", "");
				expr = " " + expr;

				float num1 = 0;
				float num2 = 0;
				;

				int dex1;
				int dex2;

				boolean isNeg = false;

				int numOps = 0;

				for (int i = 2; i < expr.length(); i++) {
					if (expr.charAt(i) == '+')
						numOps++;
					if (expr.charAt(i) == '-')
						numOps++;
					if (expr.charAt(i) == '*')
						numOps++;
					if (expr.charAt(i) == '/') {
						numOps++;
					}
				}
				if (numOps == 0)
					return expr;

				System.out.println(expr);

				for (int i = 0; i < expr.length(); i++) {
					if (i == 1 && expr.charAt(i) == '-') {
						isNeg = true;
					} else if (expr.charAt(i) == '*' || expr.charAt(i) == '/') {
						dex1 = dex2 = i;

						while (dex1 > 0 && (Character.isDigit(expr.charAt(dex1 - 1)) || expr.charAt(dex1 - 1) == '.')) {
							dex1--;
						}

						while (dex2 < expr.length() - 1
								&& (Character.isDigit(expr.charAt(dex2 + 1)) || expr.charAt(dex2 + 1) == '.')) {
							dex2++;
						}
						try {
							num1 = Float.parseFloat(expr.substring(dex1, i));
						} catch (Exception e) {
							num1 = 0;
						}

						boolean yeet = true;

						try {
							num2 = Float.parseFloat(expr.substring(i + 1, dex2 + 1));
						} catch (Exception e) {
							num2 = 0;
							yeet = false;
						}

				

						String result = "";

						if (isNeg)
							num1 = -1 * num1;

						if (expr.charAt(i) == '*') {
							result = "" + num1 * num2;

						} else {
							if (num2 == 0 && yeet)
								throw new IllegalArgumentException("Divide by zero error");
							else if (num2 == 0 && !yeet)
								num2 = 1;

							result = "" + num1 / num2;
						}

						int start = 0;
						if (isNeg)
							start = 2;

						expr = expr.substring(start, dex1) + result + expr.substring(dex2 + 1);

						i = 0;
						isNeg = false;
					}
				}

				isNeg = false;

				for (int i = 0; i < expr.length(); i++) {
					if (i == 1 && expr.charAt(i) == '-') {
						isNeg = true;
					} else if (expr.charAt(i) == '+' || expr.charAt(i) == '-') {
						dex1 = dex2 = i;

						while (dex1 > 0 && (Character.isDigit(expr.charAt(dex1 - 1)))) {
							dex1--;
						}

						while (dex2 < expr.length() - 1 && (Character.isDigit(expr.charAt(dex2 + 1)))) {
							dex2++;
						}
						try {
							num1 = Float.parseFloat(expr.substring(dex1, i));
						} catch (Exception e) {
							num1 = 0;
						}

						try {
							num2 = Float.parseFloat(expr.substring(i + 1, dex2 + 1));
						} catch (Exception e) {
							num2 = 0;
						}

						String result = "";

						if (isNeg)
							num1 = -1 * num1;

						if (expr.charAt(i) == '+') {
							result = "" + (num1 + num2);

						} else {
							result = "" + (num1 - num2);
						}

						int start = 0;
						if (isNeg)
							start = 2;

						expr = expr.substring(start, dex1) + result + expr.substring(dex2 + 1);

						i = 0;
						isNeg = false;
					}
				}

				expr = expr.replace(" ", "");

				return expr;
			} else {
				for (int i = 0; i < expr.length(); i++) {
					if (expr.charAt(i) == '[' || expr.charAt(i) == '(') {
						int begex = i;

						int skips = 1;
						i++;
						int endex = i;

						for (; endex < expr.length(); endex++) {
							if (expr.charAt(endex) == '(')
								skips++;
							if (expr.charAt(endex) == ')')
								skips--;

							if (skips == 0 && expr.charAt(endex) == ')')
								break;
						}

						String beginning = expr.substring(0, begex);
						String middle = evaluate(expr.substring(i, endex), vars, arrays);
						String end = expr.substring(endex + 1);

						return evaluate(beginning + middle + end, vars, arrays);
					}
				}
			}
		} else {
			for (int i = 0; i < expr.length(); i++) {
				if (Character.isLetter(expr.charAt(i))) {
					String currentSymbol = "";
					boolean isArray = false;

					int begex = i;

					while (i < expr.length() && Character.isLetter(expr.charAt(i))) {
						currentSymbol += expr.charAt(i);
						i++;
					}
					if (i < expr.length() && expr.charAt(i) == '[')
						isArray = true;

					if (isArray) {
						int skips = 1;
						i++;
						int endex = i;

						for (; endex < expr.length(); endex++) {
							if (expr.charAt(endex) == '[')
								skips++;
							if (expr.charAt(endex) == ']')
								skips--;

							if (skips == 0 && expr.charAt(endex) == ']')
								break;
						}

						String beforeArray = expr.substring(0, begex);
						String arrayAddress = expr.substring(i, endex);
						String afterArray = expr.substring(endex + 1);

						String evaluatedAddress = evaluate(expr.substring(i, endex), vars, arrays);

						String evaluatedArray = "";
						for (int j = 0; j < arrays.size(); j++) {
							if (arrays.get(j).name.equals(currentSymbol)) {
								evaluatedArray = "" + arrays.get(j).values[(int) Float.parseFloat(evaluatedAddress)]
										+ "";
							}
						}
						return evaluate((beforeArray + evaluatedArray + afterArray), vars, arrays);

					}
				}
			}
		}
		return "";
	}

	public static float evaluatetest(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		/** COMPLETE THIS METHOD **/
		// following line just a placeholder for compilation
		if (expr.isEmpty() || expr == null) {
			return 0;
		}
		if (expr.indexOf('[') == -1 && expr.indexOf('(') == -1) {
			expr = expr.replace(" ", "");
			expr = " " + expr;
		}
		String result = "";
		float num1, num2;
		int dex1, dex2;
		boolean isNeg = false;
		int numOps = 0;
		for (int i = 2; i < expr.length(); i++) {
			if (isOperator(expr.charAt(i))) {
				numOps++;
			}
		}
		if (numOps == 0) {
			return Float.parseFloat(expr);
		}
		for (int i = 0; i < expr.length(); i++) {
			if (expr.charAt(i) == '-' && i == 1) {
				isNeg = true;
			} else if (expr.charAt(i) == '*' || expr.charAt(i) == '/') {
				dex1 = i;
				dex2 = i;
				while (dex1 > 0 && Character.isDigit(expr.charAt(dex1 - 1))) {
					dex1--;
				}
				while (dex2 < expr.length() && Character.isDigit(expr.charAt(dex2 + 1))) {
					dex2++;
				}
				num1 = Float.parseFloat(expr.substring(dex1, i));
				num2 = Float.parseFloat(expr.substring(dex2, i));
				if (isNeg) {
					num1 *= -1;
				}
				if (expr.charAt(i) == '*') {
					result = (num1 * num2) + "";
				}
				if (expr.charAt(i) == '/' && num2 == 0) {
					throw new IllegalArgumentException("Divide by 0 error");
				} else {
					result = (num1 / num2) + "";
				}

			}
		}
		int start = 0;
		if (isNeg) {
			start = 2;
		}
		for (int i = 0; i < expr.length(); i++) {
			if (expr.charAt(i) == '-' && i == 1) {
				isNeg = true;
			} else if (expr.charAt(i) == '+' || expr.charAt(i) == '-') {
				dex1 = i;
				dex2 = i;
				while (dex1 > 0 && Character.isDigit(expr.charAt(dex1 - 1))) {
					dex1--;
				}
				while (dex2 < expr.length() - 1 && Character.isDigit(expr.charAt(dex2 + 1))) {
					dex2++;
				}
				System.out.println(expr.substring(dex1, i));
				num1 = Float.parseFloat(expr.substring(dex1, i));
				num2 = Float.parseFloat(expr.substring(i + 1, dex2 + 1));
				System.out.println(num1);
				System.out.println(num2);
				if (isNeg) {
					num1 *= -1;
				}
				if (expr.charAt(i) == '+') {
					result = (num1 + num2) + "";
				}
				if (expr.charAt(i) == '-') {
					result = (num1 - num2) + "";
				}

			}
			return Float.parseFloat(result);
		}
		expr.replace(" ", "");

		int open = 0;
		int close = 0;
		String inside = "";
		if (expr.contains(")")) {
			for (int i = 0; i < expr.length(); i++) {
				if (expr.charAt(i) == '(') {
					open = i;
				}
				if (expr.charAt(i) == ')') {
					close = i;
				}
				inside = evaluatetest(expr.substring(open + 1, close), vars, arrays) + "";
			}
		}
		return 0;

	}

	public static float evaluatetest2(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		if (expr.isEmpty() || expr == null) {
			return 0;
		}
		int numOps = 0;
		for (int i = 0; i < expr.length(); i++) {
			if (isOperator(expr.charAt(i))) {
				numOps++;
			}
		}
		if (numOps == 0) {
			return Float.parseFloat(expr);
		}
		int dex1 = 0;
		int dex2 = 0;
		float num1 = 0;
		float num2 = 0;
		float result = 0;
		for (int i = 0; i < expr.length(); i++) {
			if (expr.charAt(i) == '+' || expr.charAt(i) == '-') {
				dex1 = i;
				dex2 = i;
				System.out.println(dex1 + " " + dex2);
			}
			if ((dex1 - 1) >= 0 && (dex2 + 1) < (expr.length() - 1)) {
				dex1--;
				dex2++;
				num1 = Float.parseFloat(expr.substring(dex1, i));
				num2 = Float.parseFloat(expr.substring(i + 1, dex2 + 1));
				System.out.println(num1 + " " + num2);
			}
			if (expr.charAt(i) == '-') {
				return num1 - num2;
			} else {
				return num1 + num2;
			}
		}
		return 0;
	}

	public static boolean isOperator(char a) {
		return (a == '-' || a == '+' || a == '/' || a == '*');
	}

}