
% The LADR formulas contain function or predicate symbols that
% are not legal TPTP symbols, and we have replaced those symbols
% with new symbols.  Here is the list of the bad symbols and
% the corresponding replacements.
%
%   (arity 1)        ' -> tptp0
%   (arity 2)        + -> tptp1

cnf(sos,axiom,tptp1(A,B) = tptp1(B,A)).
cnf(sos,axiom,tptp1(tptp1(A,B),C) = tptp1(A,tptp1(B,C))).
cnf(sos,axiom,tptp0(tptp1(tptp0(tptp1(A,B)),tptp0(tptp1(A,tptp0(B))))) = A).
fof(sos,axiom,? [X0] : tptp1(X0,X0) = X0).
cnf(goals,conjecture,tptp1(tptp0(tptp1(A,tptp0(B))),tptp0(tptp1(tptp0(A),tptp0(B)))) = B).
