// Show / hide password toggle
document.querySelectorAll('.eye').forEach(function (btn) {
  btn.addEventListener('click', function () {
    var input = btn.parentElement.querySelector('input');
    var show = input.type === 'password';
    input.type = show ? 'text' : 'password';
    btn.querySelector('i').className = show ? 'bi bi-eye-slash' : 'bi bi-eye';
    btn.setAttribute('aria-label', show ? 'Hide password' : 'Show password');
  });
});
