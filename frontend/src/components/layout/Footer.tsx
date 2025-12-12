import "./Footer.css";

export default function Footer() {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="footer">
      <p>&copy; {currentYear} DHBW Inspektionsverwaltung</p>
    </footer>
  );
}
