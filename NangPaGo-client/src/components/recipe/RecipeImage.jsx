function RecipeImage({ imageRef, mainImage, recipeName }) {
  return (
    <div className="lg:w-1/2">
      <img
        ref={imageRef}
        src={mainImage}
        alt={recipeName}
        className="w-full rounded-md"
      />
    </div>
  );
}

export default RecipeImage;
