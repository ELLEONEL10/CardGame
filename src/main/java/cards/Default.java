package cards;

public class Default {
  public class C2 extends Card {}
  public class C3 extends Card {}
  public class C4 extends Card {}
  public class C5 extends Card {}
  public class C6 extends Card {}
  public class C7 extends Card {}
  public class C8 extends Card {}
  public class C9 extends Card {}
  public class C10 extends Card {}
  public class Jack extends Card {}
  public class Queen extends Card {}
  public class King extends Card {}
  public class Ace extends Card {}

  public final Card[] cards = new Card[] { 
    new C2(), new C3(), new C4(), new C5(),new C6(), 
    new C6(), new C7(), new C8(), new C9(), new C10(),
    new Jack(), new Queen(), new King(), new Ace()
  };
}
