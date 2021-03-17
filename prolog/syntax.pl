read_file(Lang,Tokens):-
  open("prolog/tokens.txt",read,S), 
  read_line_to_string(S,Lang), read_tokens(S,Tokens).

read_tokens(S,[]):-
  at_end_of_stream(S), !.
read_tokens(S,[H|T]):-
  read_line_to_string(S,H), read_tokens(S,T).

coq_term(l(X)) --> [X], {string(X)}.
coq_term(l(X)) --> ["("], coq_prop(X), [")"].

coq_prop(X) --> coq_term(X).
coq_prop(and(X,Y)) --> coq_term(X), coq_and, coq_term(Y).
coq_prop(not(X)) --> coq_not, coq_term(X).
coq_prop(not(and(not(X),not(Y)))) --> coq_term(X), coq_or, coq_term(Y).
coq_prop(not(and(X,not(Y)))) --> coq_term(X), coq_imply, coq_term(Y).
coq_prop(and(not(and(X,not(Y))),not(and(Y,not(X))))) --> coq_term(X), coq_biconditional, coq_term(Y).

coq_and --> ["/\\"].
coq_or --> ["\\/"].
coq_not --> ["~"].
coq_imply --> ["->"].
coq_biconditional --> ["<->"].

latex_term(l(X)) --> [X], {string(X)}.
latex_term(l(X)) --> ["("], latex_prop(X), [")"].

latex_prop(X) --> latex_term(X).
latex_prop(and(X,Y)) --> latex_term(X), latex_and, latex_term(Y).
latex_prop(not(X)) --> latex_not, latex_term(X).
latex_prop(not(and(not(X),not(Y)))) --> latex_term(X), latex_or, latex_term(Y).
latex_prop(not(and(X,not(Y)))) --> latex_term(X), latex_imply, latex_term(Y).
latex_prop(and(not(and(X,not(Y))),not(and(Y,not(X))))) --> latex_term(X), latex_biconditional, latex_term(Y).

latex_and --> ["\\land"].
latex_and --> ["\\wedge"].
latex_or --> ["\\lor"].
latex_or --> ["\\vee"].
latex_not --> ["\\lnot"].
latex_not --> ["\\neg"].
latex_not --> ["\\sim"].
latex_imply --> ["\\Rightarrow"].
latex_imply --> ["\\to"].
latex_imply --> ["\\rightarrow"].
latex_imply --> ["\\supset"].
latex_imply --> ["\\implies"].
latex_biconditional --> ["\\Leftrightarrow"].
latex_biconditional --> ["\\equiv"].
latex_biconditional --> ["\\leftrightarrow"].
latex_biconditional --> ["\\iff"].

functor_to_peirce(

:-read_file(Lang,Tokens),writeln(Lang),writeln(Tokens),latex_prop(X,Tokens,[]),writeln(X).

:-halt.