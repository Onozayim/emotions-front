// Users page: delete buttons carry their endpoint in data-delete-url.
document.querySelectorAll(".delete-user").forEach((button) => {
  button.addEventListener("click", () => {
    Swal.fire({
      title: "Are you sure you want to delete the user?",
      text: "You won't be able to revert this!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Yes",
    }).then((result) => {
      if (!result.isConfirmed) return;

      button.disabled = true;
      button.textContent = "Deleting…";

      fetch(button.dataset.deleteUrl, {
        method: "DELETE",
      })
        .then((response) => {
          if (!response.ok) throw new Error("HTTP " + response.status);

          Swal.fire({
            title: "Deleted!",
            text: "The user has been deleted.",
            icon: "success",
          }).then((result) => {
            window.location.reload();
          });
        })
        .catch((error) => {
          button.disabled = false;
          button.textContent = "Delete";

          Swal.fire({
            title: "Error",
            text: "The user could not be deleted. Please try again.",
            icon: "error",
          });
        });
    });
  });
});
