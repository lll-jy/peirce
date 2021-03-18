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

functor_to_peirce(X,[X]):-string(X),!.
functor_to_peirce(l(X),L):-!, functor_to_peirce(X,L).
functor_to_peirce(not(X),[frame(L)]):-!, functor_to_peirce(X,L).
functor_to_peirce(and(X,Y),L):-
  functor_to_peirce(X,XL), functor_to_peirce(Y,YL),
  append(XL,YL,L).

/*
peirce_expr_term(X,Y):-string(X), !, string_concat(X," ",Y).
peirce_expr_term(frame(L),S):-
  peirce_expression(L,FS), string_concat("[ ",FS,Temp),
  string_concat(Temp,"] ",S).
peirce_expression([],"").
peirce_expression([H|T]
*/

write_to_stream([],_):-!.
write_to_stream([H|T],O):-
  write_literal(H,O), write_to_stream(T,O).

write_literal(frame(X),O):-
  !, write(O,"[ "), write_to_stream(X,O), write(O,"] ").
write_literal(X,O):-
  string(X), write(O,X), write(O," ").

write_file(P):-
  open("prolog/peirce.txt",write,O), write_to_stream(P,O), close(O).

get_structure(S):-
  read_file("Coq",Tokens), !, coq_prop(S,Tokens,[]).
get_structure(S):-
  read_file("LeTeX",Tokens), latex_prop(S,Tokens,[]).

execute:-
  open("prolog/peirce.txt",write,O), write(O,"!!ERROR"), close(O),
  get_structure(S), functor_to_peirce(S,L), write_file(L).

:-execute.

:-halt.