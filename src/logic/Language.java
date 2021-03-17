package logic;

/**
 * Language used for the input theorem to prove, including Coq and LaTeX.
 */
public enum Language {
    Coq, LaTeX;

    @Override
    public String toString() {
        return switch (this) {
            case Coq -> "Coq";
            case LaTeX -> "LaTeX";
        };
    }
}
