import { validate } from 'class-validator';
import { IsNotBlank } from '@app/shared/decorators/validation/IsNotBlank';

class TestDto {
  @IsNotBlank()
  name: string;
}

describe('IsNotBlankDecorator', () => {
  it('should pass for non-empty string', async () => {
    const dto = new TestDto();
    dto.name = 'Valid';
    const errors = await validate(dto);
    expect(errors.length).toBe(0);
  });

  it('should fail for empty string', async () => {
    const dto = new TestDto();
    dto.name = '';
    const errors = await validate(dto);
    expect(errors.length).toBeGreaterThan(0);
  });

  it('should fail for whitespace only string', async () => {
    const dto = new TestDto();
    dto.name = '   ';
    const errors = await validate(dto);
    expect(errors.length).toBeGreaterThan(0);
  });
});
