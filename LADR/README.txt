The bin folder contains Prover9, Mace4, and related programs
compiled with gcc in Cygwin under Windows.  The binaries use
cygwin1.dll (also in bin).  To test, get a command prompt,
go this folder, and run

    bin\prover9 -f x2.in > x2.out
    bin\mace4 -f rw1.in > rw1.out

The outputs should be similar to the corresponding out_save files.

If there is an out-of-date copy of cygwin1.dll on your computer, the
programs might not work.

The copyright terms of Cygwin apply to the Cygwin dll.

For information on Prover9 and Mace4, including the source code,
examples, and documentation, see

    http://www.cs.unm.edu/~mccune/prover9

A copy of the manual (HTML) is included in this package: open
the file index.html in this directory.
