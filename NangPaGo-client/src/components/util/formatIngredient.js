export function formatIngredient(ingredient) {
  return ingredient.replace(/[^가-힣a-zA-Z0-9()./×\s]/gi, '').trim();
}
